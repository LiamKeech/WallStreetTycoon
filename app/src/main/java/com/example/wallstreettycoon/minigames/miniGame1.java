package com.example.wallstreettycoon.minigames;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

import com.example.wallstreettycoon.R;

import java.util.Random;

public class miniGame1 extends AppCompatActivity {
    FrameLayout container;
    Random random = new Random();

    private Handler handler = new Handler();  // use main looper by default
    private int spawnCount = 0;
    private final int MAX_SPAWNS = 100;
    private final int SPAWN_DELAY_MS = 500;

    private final Runnable spawnTask = new Runnable() {
        @Override
        public void run() {
            spawnFloatingButton();
            spawnCount++;

            if (spawnCount < MAX_SPAWNS) {
                handler.postDelayed(this, SPAWN_DELAY_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game1);

        container = findViewById(R.id.container);

        // Start spawning once the layout is ready
        container.post(() -> handler.post(spawnTask));
    }

    private void spawnFloatingButton() {
        Button button = new Button(this);
        button.setText("Btn");

        int btnWidthDp = 100;
        int btnHeightDp = 60;

        int btnWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnWidthDp, getResources().getDisplayMetrics());
        int btnHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnHeightDp, getResources().getDisplayMetrics());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(btnWidthPx, btnHeightPx);

        int maxX = container.getWidth() - btnWidthPx;
        int randomX = random.nextInt(Math.max(maxX, 1));

        params.leftMargin = randomX;
        params.gravity = Gravity.BOTTOM;

        button.setLayoutParams(params);
        container.addView(button);

        button.animate()
                .translationY(-container.getHeight() - btnHeightPx)
                .setDuration(4000)
                .withEndAction(() -> container.removeView(button))
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(spawnTask); // clean up handler
    }
}