package com.example.wallstreettycoon.minigames;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wallstreettycoon.R;

import java.util.Random;

public class miniGame1 extends AppCompatActivity {

    Button buyButton;
    Button startButton;
    Handler handler = new Handler(Looper.getMainLooper());
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game1);

        startButton = findViewById(R.id.start_button);
        buyButton = findViewById(R.id.buy_button);

        startButton.setOnClickListener(view -> {
            startButton.setVisibility(View.INVISIBLE);
            flashButtonAfterRandomDelay();
        });

    }

    private void flashButtonAfterRandomDelay() {
        // Generate a random delay between 1 and 2 seconds
        int delayMillis = 1000 + random.nextInt(2000);

        handler.postDelayed(() -> {
            buyButton.setVisibility(View.VISIBLE);
            hideButtonAfterRandomDelay();
            flashButtonAfterRandomDelay();
        }, delayMillis);


    }
    private void hideButtonAfterRandomDelay() {
        int delayMillis = 200 + random.nextInt(400);

        handler.postDelayed(() -> buyButton.setVisibility(View.INVISIBLE), delayMillis);
    }
}