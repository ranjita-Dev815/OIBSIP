package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // needed for ON DELETE CASCADE
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT NOT NULL UNIQUE," +
                "salt TEXT NOT NULL," +
                "password_hash TEXT NOT NULL)");
        db.execSQL("CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "notes TEXT," +
                "completed INTEGER NOT NULL DEFAULT 0," +
                "created_at INTEGER NOT NULL," +
                "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // ---------- Password hashing (salted SHA-256) ----------

    private static String newSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    private static String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- Users ----------

    /** @return true if registered, false if the email is already taken. */
    public boolean registerUser(String name, String email, String password) {
        if (emailExists(email)) return false;
        String salt = newSalt();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("salt", salt);
        cv.put("password_hash", hash(password, salt));
        return getWritableDatabase().insert("users", null, cv) != -1;
    }

    private boolean emailExists(String email) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT 1 FROM users WHERE email = ?", new String[]{email})) {
            return c.moveToFirst();
        }
    }

    /** @return the User on success, or null for bad credentials. */
    public User login(String email, String password) {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, salt, password_hash FROM users WHERE email = ?",
                new String[]{email})) {
            if (!c.moveToFirst()) return null;
            String salt = c.getString(2);
            String stored = c.getString(3);
            if (MessageDigest.isEqual(
                    stored.getBytes(StandardCharsets.UTF_8),
                    hash(password, salt).getBytes(StandardCharsets.UTF_8))) {
                return new User(c.getLong(0), c.getString(1));
            }
            return null;
        }
    }

    // ---------- Tasks (always scoped by user_id) ----------

    public void addTask(long userId, String title, String notes) {
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("title", title);
        cv.put("notes", notes);
        cv.put("completed", 0);
        cv.put("created_at", System.currentTimeMillis());
        getWritableDatabase().insert("tasks", null, cv);
    }

    /** Active tasks first (newest on top), completed tasks after. */
    public List<Task> getTasks(long userId) {
        List<Task> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, title, notes, completed FROM tasks WHERE user_id = ? " +
                        "ORDER BY completed ASC, created_at DESC",
                new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()) {
                list.add(new Task(c.getLong(0), c.getString(1), c.getString(2), c.getInt(3) == 1));
            }
        }
        return list;
    }

    public void setCompleted(long userId, long taskId, boolean completed) {
        ContentValues cv = new ContentValues();
        cv.put("completed", completed ? 1 : 0);
        getWritableDatabase().update("tasks", cv, "id = ? AND user_id = ?",
                new String[]{String.valueOf(taskId), String.valueOf(userId)});
    }

    public void deleteTask(long userId, long taskId) {
        getWritableDatabase().delete("tasks", "id = ? AND user_id = ?",
                new String[]{String.valueOf(taskId), String.valueOf(userId)});
    }
}
