package com.example.wallstreettycoon.model;

import static com.example.wallstreettycoon.model.Game.dbUtil;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

public class Timer {
    public long startTime;
    private long elapsedTime = 0;
    private boolean isPaused = false;
    private int currentEventIndex = 0;
    private List<MarketEvent> marketEventList;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateTimeStampRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimestamp();
            handler.postDelayed(this, 3000);
        }
    };

    public Timer() {
        startTimer();
    }

    public Timer(boolean autoStart) {
        if (autoStart) startTimer();
    }

    public long getElapsedTime(){ //returns elapsed time in seconds
        elapsedTime = System.nanoTime() - startTime;
        return elapsedTime / 1000000000;
    }

    public void startTimer() {
        startTime = System.nanoTime() - elapsedTime; // resume properly
        isPaused = false;
        handler.post(updateTimeStampRunnable);

        if (marketEventList == null) {
            marketEventList = dbUtil.getMarketEvents();
        }

        handler.postDelayed(() -> {
            scheduleNextEvent(currentEventIndex);
        },  5000L);
    }

    public void pauseTimer() {
        elapsedTime = System.nanoTime() - startTime; // store elapsed time
        isPaused = true;
        handler.removeCallbacks(updateTimeStampRunnable);
    }

    public void updateTimestamp(){
        Log.d("TIMER UPDATED", String.valueOf(getElapsedTime()));
        Game.getInstance().onGameEvent(new GameEvent(GameEventType.UPDATE_STOCK_PRICE, "Price updated", (int)getElapsedTime()));
    }

    private void scheduleNextEvent(int index) {
        Log.d("SCHEDULE NEXT EVENT", String.valueOf(index));
        if (marketEventList == null || index >= marketEventList.size()) return;

        currentEventIndex = index; // save position

        MarketEvent event = marketEventList.get(index);
        GameEvent currentEvent = new GameEvent(
                GameEventType.MARKET_EVENT,
                event.getTitle(),
                event
        );

        // dispatch on main thread
        Game.getInstance().onGameEvent(currentEvent);

        // schedule the next one
        handler.postDelayed(() -> {
            if (!isPaused) {
                scheduleNextEvent(index + 1);
            } else {
                // wait until resumed (don’t lose position)
                currentEventIndex = index + 1;
            }
        }, event.getDuration() * 1000L);
    }

    public int getCurrentEventIndex(){
        return currentEventIndex;
    }

    public void resumeFrom(long savedElapsedTime, int currentEventIndex){
        this.currentEventIndex = currentEventIndex;
        this.elapsedTime = savedElapsedTime * 1_000_000_000L;
        startTimer();
    }
}
