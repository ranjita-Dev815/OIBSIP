package com.example.stopwatch; // change to your package name

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_RUNNING = "running";
    private static final String KEY_ACCUMULATED = "accumulated";
    private static final String KEY_START = "startTime";
    private static final String KEY_LAPS = "laps";

    private TextView tvTime;
    private Button btnStart, btnStop, btnLap, btnReset;
    private ListView lvLaps;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ArrayList<String> laps = new ArrayList<>();
    private ArrayAdapter<String> lapAdapter;

    private boolean running = false;
    private long startTime = 0;      // elapsedRealtime when last started
    private long accumulated = 0;    // time banked from previous runs (ms)

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            tvTime.setText(format(currentElapsed()));
            handler.postDelayed(this, 10); // update every 10 ms
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTime = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnLap = findViewById(R.id.btnLap);
        btnReset = findViewById(R.id.btnReset);
        lvLaps = findViewById(R.id.lvLaps);

        if (savedInstanceState != null) {
            running = savedInstanceState.getBoolean(KEY_RUNNING);
            accumulated = savedInstanceState.getLong(KEY_ACCUMULATED);
            startTime = savedInstanceState.getLong(KEY_START);
            ArrayList<String> saved = savedInstanceState.getStringArrayList(KEY_LAPS);
            if (saved != null) laps = saved;
        }

        lapAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, laps);
        lvLaps.setAdapter(lapAdapter);

        btnStart.setOnClickListener(v -> start());
        btnStop.setOnClickListener(v -> pause());
        btnLap.setOnClickListener(v -> lap());
        btnReset.setOnClickListener(v -> reset());

        tvTime.setText(format(currentElapsed()));
        updateButtons();
    }

    private void start() {
        if (running) return;
        startTime = SystemClock.elapsedRealtime();
        running = true;
        handler.post(ticker);
        updateButtons();
    }

    private void pause() {
        if (!running) return;
        accumulated += SystemClock.elapsedRealtime() - startTime;
        running = false;
        handler.removeCallbacks(ticker);
        tvTime.setText(format(accumulated));
        updateButtons();
    }

    private void reset() {
        handler.removeCallbacks(ticker);
        running = false;
        accumulated = 0;
        startTime = 0;
        laps.clear();
        lapAdapter.notifyDataSetChanged();
        tvTime.setText(format(0));
        updateButtons();
    }

    private void lap() {
        if (!running) return;
        laps.add(0, "Lap " + (laps.size() + 1) + "   " + format(currentElapsed()));
        lapAdapter.notifyDataSetChanged();
    }

    private long currentElapsed() {
        return running ? accumulated + (SystemClock.elapsedRealtime() - startTime) : accumulated;
    }

    private void updateButtons() {
        btnStart.setEnabled(!running);   // greyed out while running
        btnStop.setEnabled(running);     // active only while running
        btnLap.setEnabled(running);
    }

    private String format(long ms) {
        long hundredths = (ms / 10) % 100;
        long totalSec = ms / 1000;
        long s = totalSec % 60;
        long m = (totalSec / 60) % 60;
        long h = totalSec / 3600;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d.%02d", h, m, s, hundredths);
    }

    // Lifecycle: the time is computed from the system clock, so it stays correct
    // while the app is in the background. We only stop/start the UI updates.
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(ticker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvTime.setText(format(currentElapsed()));
        if (running) handler.post(ticker);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_RUNNING, running);
        outState.putLong(KEY_ACCUMULATED, accumulated);
        outState.putLong(KEY_START, startTime);
        outState.putStringArrayList(KEY_LAPS, laps);
    }
}