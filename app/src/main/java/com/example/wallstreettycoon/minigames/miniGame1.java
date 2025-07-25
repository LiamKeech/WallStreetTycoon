package com.example.wallstreettycoon.minigames;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.animation.ValueAnimator;
import android.widget.TextView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.stock.Stock;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import kotlin.collections.ArrayDeque;

public class miniGame1 extends AppCompatActivity {

    FrameLayout container;
    Random random = new Random();
    Map<Stock, Float> stockBoughtPrice = new HashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game1);



        Context context = this;
        DatabaseUtil dbUtil = new DatabaseUtil(context);

        container = findViewById(R.id.container);
        List<Stock> stockList = dbUtil.getStockList();

        Log.d(stockList.getFirst().getStockName(), "");
        int delay = 600;
        for(Stock stock: stockList){ //TODO change to just technology stocks
            handler.postDelayed(() -> {
                spawnFloatingButton(stock);
            }, delay);
            delay += 600;
        }

    }

    private void spawnFloatingButton(Stock stock) {
        //Add stock to held stocks after clicked at price when it is clicked
        LinearLayout buyListLayout = findViewById(R.id.buy_list_layout);
        Button button = new Button(this);

        int btnSize = 100;

        //Makes sure the buttons are always the same size on different devices
        int btnWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnSize, getResources().getDisplayMetrics());
        int btnHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnSize, getResources().getDisplayMetrics());
        int btnTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());

        //make buttons circles
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#48C73C")); // green
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

            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize * scale);
            button.setLayoutParams(btnParams);
        });
        AtomicReference<Float> currentPrice = new AtomicReference<>(0f);

        // Animate the price of stock
        ValueAnimator priceAnimator = ValueAnimator.ofFloat(0, 50);
        priceAnimator.setDuration(4000);
        priceAnimator.setInterpolator(new AccelerateInterpolator());//can change later depending on gameplay
        priceAnimator.addUpdateListener(animation -> {
            float price = (float) animation.getAnimatedValue();
            button.setText(String.format("Buy %s $%.2f",stock.getSymbol(), price));
            currentPrice.set(price);
            });

        // Start animations together
        priceAnimator.start();
        resizeAnimator.start();
        button.animate()
                .translationY(-container.getHeight() - btnHeightPx)
                .setDuration(4000)
                .withEndAction(() -> container.removeView(button))
                .start();

        button.setOnClickListener(v -> {
            shape.setColor(Color.parseColor("#FF6417")); // orange

            Float boughtPrice = currentPrice.get();
            stockBoughtPrice.put(stock, boughtPrice);

            Log.d(stock.getStockName() + " bought at: " + String.valueOf(currentPrice.get()), "");

            TextView stockView = new TextView(this);
            stockView.setText(String.format("%s: $%.2f", stock.getSymbol(), boughtPrice));
            stockView.setTextColor(Color.WHITE); // Or any style you want
            stockView.setTextSize(14);
            buyListLayout.addView(stockView, 0);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}