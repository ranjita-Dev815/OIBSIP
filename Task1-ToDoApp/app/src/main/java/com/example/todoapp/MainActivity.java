package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.Listener {
    private DatabaseHelper db;
    private SessionManager session;
    private TaskAdapter adapter;
    private View emptyState;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) { // guard: no session -> back to login
            goToLogin();
            return;
        }
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);

        ((TextView) findViewById(R.id.tvGreeting))
                .setText(getString(R.string.greeting, session.getUserName()));

        recycler = findViewById(R.id.recyclerTasks);
        emptyState = findViewById(R.id.emptyState);
        adapter = new TaskAdapter(this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        Button btnAdd = findViewById(R.id.btnAddTask);
        Button btnLogout = findViewById(R.id.btnLogout);
        btnAdd.setOnClickListener(v -> showAddDialog());
        btnLogout.setOnClickListener(v -> {
            session.logout();
            goToLogin();
        });

        refresh();
    }

    private void refresh() {
        List<Task> tasks = db.getTasks(session.getUserId());
        adapter.submit(tasks);
        boolean empty = tasks.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText etTitle = view.findViewById(R.id.etTaskTitle);
        EditText etNotes = view.findViewById(R.id.etTaskNotes);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add_task)
                .setView(view)
                .setPositiveButton(R.string.save, (d, w) -> {
                    String title = etTitle.getText().toString().trim();
                    String notes = etNotes.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(this, R.string.task_name_required, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.addTask(session.getUserId(), title, notes);
                    refresh();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onToggle(Task task, boolean completed) {
        db.setCompleted(session.getUserId(), task.id, completed);
        // post so we don't notify the adapter while RecyclerView is mid-callback
        recycler.post(this::refresh);
    }

    @Override
    public void onDelete(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_task)
                .setMessage(getString(R.string.delete_confirm, task.title))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    db.deleteTask(session.getUserId(), task.id);
                    refresh();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void goToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
