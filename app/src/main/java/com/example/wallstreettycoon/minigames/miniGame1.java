package com.example.wallstreettycoon.minigames;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.animation.ValueAnimator;

import com.example.wallstreettycoon.R;

import java.util.Random;

public class miniGame1 extends AppCompatActivity {
    FrameLayout container;
    Random random = new Random();

    private Handler handler = new Handler(Looper.getMainLooper());
    private int spawnCount = 0;
    private final int MAX_SPAWNS = 100;
    private final int SPAWN_DELAY_MS = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game1);

        container = findViewById(R.id.container);

        // Start spawning once the layout is ready
        container.post(() -> handler.post(spawnTask));
    }
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

    private void spawnFloatingButton() {
        Button button = new Button(this);
        button.setText("Buy now!");

        int btnSize = 100;

        //Makes sure the buttons are always the same size on different devices
        int btnWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnSize, getResources().getDisplayMetrics());
        int btnHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnSize, getResources().getDisplayMetrics());

        //make buttons circles
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#FF6200EE")); // purple
        button.setBackground(shape);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(btnWidthPx, btnHeightPx);

        button.setTextColor(Color.WHITE);

        //get a random point on the x axis that is on the screen
        int maxX = container.getWidth() - btnWidthPx;
        int randomX = random.nextInt(Math.max(maxX, 1));


        params.leftMargin = randomX;
        params.gravity = Gravity.BOTTOM;

        button.setLayoutParams(params);
        container.addView(button);

        // Animate size using ValueAnimator
        ValueAnimator resizeAnimator = ValueAnimator.ofFloat(0.5f, 1f);  // scale from full size to 0
        resizeAnimator.setDuration(4000);
        resizeAnimator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            ViewGroup.LayoutParams btnParams = button.getLayoutParams();
            btnParams.width = (int) (btnWidthPx * scale);
            btnParams.height = (int) (btnHeightPx * scale);
            button.setLayoutParams(btnParams);
        });

        // Start both animations together
        resizeAnimator.start();
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