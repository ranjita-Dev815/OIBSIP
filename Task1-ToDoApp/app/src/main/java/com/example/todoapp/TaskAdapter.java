package com.example.todoapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.Holder> {

    public interface Listener {
        void onToggle(Task task, boolean completed);
        void onDelete(Task task);
    }

    private final List<Task> tasks = new ArrayList<>();
    private final Listener listener;

    public TaskAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Task t = tasks.get(position);
        h.title.setText(t.title);

        boolean hasNotes = t.notes != null && !t.notes.trim().isEmpty();
        h.notes.setVisibility(hasNotes ? View.VISIBLE : View.GONE);
        if (hasNotes) h.notes.setText(t.notes);

        // Strikethrough + dimmed when complete
        if (t.completed) {
            h.title.setPaintFlags(h.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            h.title.setPaintFlags(h.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        float alpha = t.completed ? 0.5f : 1f;
        h.title.setAlpha(alpha);
        h.notes.setAlpha(alpha);

        // Detach listener before setting state to avoid firing during recycling
        h.check.setOnCheckedChangeListener(null);
        h.check.setChecked(t.completed);
        h.check.setOnCheckedChangeListener((b, isChecked) -> listener.onToggle(t, isChecked));
        h.delete.setOnClickListener(v -> listener.onDelete(t));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final CheckBox check;
        final TextView title, notes;
        final ImageButton delete;

        Holder(@NonNull View v) {
            super(v);
            check = v.findViewById(R.id.cbDone);
            title = v.findViewById(R.id.tvTitle);
            notes = v.findViewById(R.id.tvNotes);
            delete = v.findViewById(R.id.btnDelete);
        }
    }
}
