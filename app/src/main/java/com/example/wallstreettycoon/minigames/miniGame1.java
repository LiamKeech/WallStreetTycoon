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
    Handler handler = new Handler(Looper.getMainLooper());
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game1);

        buyButton = findViewById(R.id.my_button);

        // Start the random delay task
        showButtonAfterRandomDelay();
    }

    private void showButtonAfterRandomDelay() {
        // Generate a random delay between 1 and 5 seconds
        int delayMillis = 1000 + random.nextInt(4000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                buyButton.setVisibility(View.VISIBLE);
            }
        }, delayMillis);
    }
}