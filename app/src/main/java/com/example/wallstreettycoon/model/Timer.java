package com.example.wallstreettycoon.model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Timer {
    public long startTime;
    private long elapsedTime = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateTimestamp();
            handler.postDelayed(this, 3000);
        }
    };

    public Timer(){
        startTimer();
        handler.post(runnable);
    }

    public long getElapsedTime(){ //returns elapsed time in seconds
        elapsedTime = System.nanoTime() - startTime;
        return elapsedTime / 1000000000;
    }

    public void startTimer(){
        startTime = System.nanoTime() + elapsedTime;
    }

    public void updateTimestamp(){
        Log.d("TIMER UPDATED", String.valueOf(getElapsedTime()));
        Game.getInstance().onGameEvent(new GameEvent(GameEventType.UPDATE_STOCK_PRICE, "Price updated", (int)getElapsedTime()));
    }
}
