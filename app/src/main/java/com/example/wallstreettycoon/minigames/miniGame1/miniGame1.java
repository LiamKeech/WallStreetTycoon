package com.example.wallstreettycoon.minigames.miniGame1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.res.ResourcesCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.animation.ValueAnimator;
import android.widget.TextView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.stock.Stock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class miniGame1 extends AppCompatActivity {
    LinearLayout buyListLayout;
    TextView profitLabel;
    Map<Stock, TextView> stockTextViews = new HashMap<>();
    FrameLayout container;
    Random random = new Random();
    Map<Stock, Float> stockBoughtPrice = new HashMap<>();
    Float profit = 0f;
    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game1);

        Context context = this;
        DatabaseUtil dbUtil = DatabaseUtil.getInstance(context); // SINGLETON FIX

        container = findViewById(R.id.container);
        List<Stock> stockList = dbUtil.getStockListByCategory("Technology");

        int delay = 1000;
        int increment = 1000;
        for(Stock stock: stockList){
            handler.postDelayed(() -> {
                spawnFloatingButton(stock);
            }, delay);
            delay += increment;
        }

        handler.postDelayed(() -> {
            Double balance = Game.getInstance().currentUser().getUserBalance();
            DatabaseUtil.getInstance(this).updateBalance(balance + profit, Game.getInstance().currentUser().getUserUsername());
            miniGame1EndDialogFragment endDialogFragment = new miniGame1EndDialogFragment();
            endDialogFragment.setCancelable(false);

            Bundle bundle = new Bundle();
            bundle.putFloat("profit", profit);
            endDialogFragment.setArguments(bundle);

            endDialogFragment.show(getSupportFragmentManager(), "miniGame1End");

        }, stockList.size() * (1000 + 100) + 5000);
    }

    private void spawnFloatingButton(Stock stock) {
        buyListLayout = findViewById(R.id.buy_list_layout);

        profitLabel = findViewById(R.id.profit_label);
        profitLabel.setText(String.format("Profit: $%.2f", profit));

        Button button = new Button(this);
        AtomicBoolean held = new AtomicBoolean(false);

        int btnSize = 100;

        //Makes sure the buttons are always the same size on different devices
        int btnWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnSize, getResources().getDisplayMetrics());
        int btnHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, btnSize, getResources().getDisplayMetrics());
        int btnTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());

        //make buttons circles
        button.setBackground(getResources().getDrawable(R.drawable.minigame_1_btn_green, null));
        button.setTypeface(ResourcesCompat.getFont(this, R.font.jua));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(btnWidthPx, btnHeightPx);

        button.setTextColor(Color.WHITE);

        //get a random point on the x axis that is on the screen
        int maxX = container.getWidth() - btnWidthPx;
        int randomX = random.nextInt(Math.max(maxX, 1));


        params.leftMargin = randomX;
        params.gravity = Gravity.BOTTOM;

        button.setLayoutParams(params);
        container.addView(button);

        AtomicReference<Float> currentPrice = new AtomicReference<>(0f);

        increasePrice(button, currentPrice, held, stock, btnWidthPx, btnHeightPx, btnTextSize, () -> {
            decreasePrice(button, currentPrice, held, stock, btnWidthPx, btnHeightPx, btnTextSize);
        });



        button.setOnClickListener(v -> {
            if(!held.get()) {
                addStockToHeldStocksList(held, button, currentPrice, stock);
            }
            else{
                removeStockFromHeldStocksList(held, button, currentPrice, stock);
            }
        });
    }

    public void increasePrice(Button button, AtomicReference<Float> currentPrice, AtomicBoolean held, Stock stock, int btnWidthPx, int btnHeightPx, int btnTextSize, Runnable onComplete){
        // animate size increasing
        ValueAnimator resizeAnimator = ValueAnimator.ofFloat(0.5f, 1f);
        resizeAnimator.setDuration(4000);
        resizeAnimator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();

            ViewGroup.LayoutParams btnParams = button.getLayoutParams();
            btnParams.width = (int) (btnWidthPx * scale);
            btnParams.height = (int) (btnHeightPx * scale);

            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize * scale);
            button.setLayoutParams(btnParams);
        });
        //end region

        // animate the price increasing
        ValueAnimator priceIncreaseAnimator = ValueAnimator.ofFloat(0, random.nextFloat(100) + 100);
        priceIncreaseAnimator.setDuration(4000);
        priceIncreaseAnimator.setInterpolator(new AccelerateInterpolator());//can change later depending on gameplay
        priceIncreaseAnimator.addUpdateListener(animation -> {
            float price = (float) animation.getAnimatedValue();
            if(!held.get())
                button.setText(String.format("Buy %s $%.2f",stock.getSymbol(), price));
            else
                button.setText(String.format("Sell %s $%.2f",stock.getSymbol(), price));
            currentPrice.set(price);
        });
        //end region

        // When price increase finishes, run the callback
        priceIncreaseAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onComplete != null) onComplete.run();
            }
        });

        // start animations together
        priceIncreaseAnimator.start();
        resizeAnimator.start();
        button.animate()
                .translationY(-container.getHeight() + btnHeightPx)
                .setDuration(4000)
                .start();
    }
    public void decreasePrice(Button button, AtomicReference<Float> currentPrice, AtomicBoolean held, Stock stock, int btnWidthPx, int btnHeightPx, int btnTextSize){
        // animate size increasing
        ValueAnimator resizeAnimator = ValueAnimator.ofFloat(1f, 0.5f);
        resizeAnimator.setDuration(1000);
        resizeAnimator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();

            ViewGroup.LayoutParams btnParams = button.getLayoutParams();
            btnParams.width = (int) (btnWidthPx * scale);
            btnParams.height = (int) (btnHeightPx * scale);

            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize * scale);
            button.setLayoutParams(btnParams);
        });
        //end region

        // animate the price increasing
        ValueAnimator priceDecreaseAnimator = ValueAnimator.ofFloat(currentPrice.get(), random.nextFloat(5));
        priceDecreaseAnimator.setDuration(1000);
        priceDecreaseAnimator.setInterpolator(new AccelerateInterpolator());//can change later depending on gameplay
        priceDecreaseAnimator.addUpdateListener(animation -> {
            float price = (float) animation.getAnimatedValue();
            if(!held.get())
                button.setText(String.format("Buy %s $%.2f",stock.getSymbol(), price));
            else
                button.setText(String.format("Sell %s $%.2f",stock.getSymbol(), price));
            currentPrice.set(price);
        });
        //end region

        // start animations together
        priceDecreaseAnimator.start();
        resizeAnimator.start();
        button.animate()
                .translationY(0)
                .setDuration(1000)
                .start();

        button.postDelayed(() -> {
            removeStockFromHeldStocksList(held, button, currentPrice, stock);

            container.removeView(button);
        }, 3000);
    }
    public void addStockToHeldStocksList(AtomicBoolean held, Button button, AtomicReference<Float> currentPrice, Stock stock){
        held.set(true);
        button.setBackground(getResources().getDrawable(R.drawable.minigame_1_btn_orange, null));

        Float boughtPrice = currentPrice.get();
        stockBoughtPrice.put(stock, boughtPrice);

        profit -= boughtPrice;

        Context styledContext = new ContextThemeWrapper(this, R.style.LightBlueTextView);
        TextView stockView = new TextView(styledContext);
        int paddingHorizontal = (int) (20 * styledContext.getResources().getDisplayMetrics().density + 0.5f);
        int paddingVertical   = (int) (10 * styledContext.getResources().getDisplayMetrics().density + 0.5f);

        stockView.setPadding(paddingHorizontal, paddingVertical,
                paddingHorizontal, paddingVertical);
        stockView.setGravity(Gravity.CENTER);

        stockView.setText(String.format("%s: $%.2f", stock.getSymbol(), boughtPrice));
        stockView.setTextSize(15);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 0, 0, 3);
        params.gravity = Gravity.CENTER;
        stockView.setLayoutParams(params);

        buyListLayout.addView(stockView);
        stockTextViews.put(stock, stockView);


        if(profit >= 0){
            profitLabel.setText(String.format("Profit: $%.2f", profit));

            int paddingTop = profitLabel.getPaddingTop();
            int paddingBottom = profitLabel.getPaddingBottom();
            int paddingLeft = profitLabel.getPaddingLeft();
            int paddingRight = profitLabel.getPaddingRight();

            profitLabel.setBackgroundResource(R.drawable.button_background_green_medium);
            profitLabel.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
        else{
            profitLabel.setText(String.format("Loss: $%.2f", profit));

            int paddingTop = profitLabel.getPaddingTop();
            int paddingBottom = profitLabel.getPaddingBottom();
            int paddingLeft = profitLabel.getPaddingLeft();
            int paddingRight = profitLabel.getPaddingRight();

            profitLabel.setBackgroundResource(R.drawable.button_background_red_medium);
            profitLabel.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }
    public void removeStockFromHeldStocksList(AtomicBoolean held, Button button, AtomicReference<Float> currentPrice, Stock stock){
        if(held.get()) {
            held.set(false);
            button.setBackground(getResources().getDrawable(R.drawable.minigame_1_btn_green, null));

            Float sellPrice = currentPrice.get();
            profit += sellPrice;
            //remove from held stocks
            stockBoughtPrice.remove(stock);

            // Remove the TextView from layout
            TextView viewToRemove = stockTextViews.get(stock);
            if (viewToRemove != null) {
                buyListLayout.removeView(viewToRemove);
                stockTextViews.remove(stock);
            }

            if(profit >= 0){
                profitLabel.setText(String.format("Profit: $%.2f", profit));

                int paddingTop = profitLabel.getPaddingTop();
                int paddingBottom = profitLabel.getPaddingBottom();
                int paddingLeft = profitLabel.getPaddingLeft();
                int paddingRight = profitLabel.getPaddingRight();

                profitLabel.setBackgroundResource(R.drawable.button_background_green_medium);
                profitLabel.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }
            else{
                profitLabel.setText(String.format("Loss: $%.2f", profit));

                int paddingTop = profitLabel.getPaddingTop();
                int paddingBottom = profitLabel.getPaddingBottom();
                int paddingLeft = profitLabel.getPaddingLeft();
                int paddingRight = profitLabel.getPaddingRight();

                profitLabel.setBackgroundResource(R.drawable.button_background_red_medium);
                profitLabel.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}