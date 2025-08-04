package com.example.wallstreettycoon.minigames;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.animation.ValueAnimator;
import android.widget.TextView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.displayBuySell.SellDialogFragment;
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
        DatabaseUtil dbUtil = new DatabaseUtil(context);

        container = findViewById(R.id.container);
        List<Stock> stockList = dbUtil.getStockList();

        int delay = 1000;
        int increment = 1000;
        for(Stock stock: stockList){ //TODO change to just technology stocks
            handler.postDelayed(() -> {
                spawnFloatingButton(stock);
            }, delay);
            delay += increment;
        }

        handler.postDelayed(() -> {
            miniGame1EndDialogFragment endDialogFragment = new miniGame1EndDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putFloat("profit", profit);
            endDialogFragment.setArguments(bundle);

            endDialogFragment.show(getSupportFragmentManager(), "miniGame1End");

        }, stockList.size() * (1000 + 100) + 2000);
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

        AtomicReference<Float> currentPrice = new AtomicReference<>(0f);

        increasePrice(button, currentPrice, held, stock, btnWidthPx, btnHeightPx, btnTextSize, () -> {
            decreasePrice(button, currentPrice, held, stock, btnWidthPx, btnHeightPx, btnTextSize);
        });



        button.setOnClickListener(v -> {
            if(!held.get()) {
                addStockToHeldStocksList(held, shape, currentPrice, stock);
            }
            else{
                removeStockFromHeldStocksList(held, shape, currentPrice, stock);
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
            removeStockFromHeldStocksList(held, (GradientDrawable) button.getBackground(), currentPrice, stock);

            container.removeView(button);
        }, 3000);
    }
    public void addStockToHeldStocksList(AtomicBoolean held, GradientDrawable shape, AtomicReference<Float> currentPrice, Stock stock){
        held.set(true);
        shape.setColor(Color.parseColor("#FF6417")); // orange

        Float boughtPrice = currentPrice.get();
        stockBoughtPrice.put(stock, boughtPrice);

        profit -= boughtPrice;

        TextView stockView = new TextView(this);
        stockView.setText(String.format("%s: $%.2f", stock.getSymbol(), boughtPrice));
        stockView.setTextColor(Color.BLACK);
        stockView.setTextSize(14);
        buyListLayout.addView(stockView);
        stockTextViews.put(stock, stockView);

        profitLabel.setText(String.format("Profit: $%.2f", profit));
    }
    public void removeStockFromHeldStocksList(AtomicBoolean held, GradientDrawable shape, AtomicReference<Float> currentPrice, Stock stock){
        if(held.get()) {
            held.set(false);
            shape.setColor(Color.parseColor("#48C73C")); // green

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

            profitLabel.setText(String.format("Profit: $%.2f", profit));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}