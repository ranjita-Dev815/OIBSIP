package com.example.todoapp;

import android.content.Context;
import android.content.SharedPreferences;

/** Keeps track of who is logged in. Cleared on logout. */
public class SessionManager {
    private static final String PREFS = "session";
    private static final String KEY_ID = "user_id";
    private static final String KEY_NAME = "user_name";
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void login(User user) {
        prefs.edit().putLong(KEY_ID, user.id).putString(KEY_NAME, user.name).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getLong(KEY_ID, -1) != -1;
    }

    public long getUserId() {
        return prefs.getLong(KEY_ID, -1);
    }

    public String getUserName() {
        return prefs.getString(KEY_NAME, "");
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
