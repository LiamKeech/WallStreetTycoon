/**
 * Author: Gareth Munnings
 * Created on 2025/09/16
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

public class Timer {
    private long remainingTime; // in milliseconds
    private long endTime;       //system time when the timer should end
    private long maxTime;
    private boolean running;
    private Runnable onFinish;
    private Thread workerThread;


    public Timer(long maxTime, Runnable onFinish) {
        this.maxTime = maxTime;
        this.remainingTime = maxTime;
        this.onFinish = onFinish;
        this.running = false;
    }

    // Start or resume the timer
    public synchronized void start() {
        if (running) return;

        running = true;
        endTime = System.currentTimeMillis() + remainingTime;

        workerThread = new Thread(() -> {
            while (running && getTime() > 0) {
                try {
                    Thread.sleep(100); // check every 100ms
                } catch (InterruptedException ignored) {}
            }
            if (running) {
                running = false;
                if (onFinish != null) {
                    onFinish.run(); // trigger event
                }
            }
        });
        workerThread.start();
    }

    public synchronized void stop() {
        if (running) {
            remainingTime = getTime(); // store leftover time
            running = false;
        }
    }

    public synchronized void addTime(long millis) {
        if(getTime() + millis > maxTime)
            remainingTime = maxTime;
        else
            remainingTime = getTime() + millis;
        if (running) {
            endTime = System.currentTimeMillis() + remainingTime;
        }
    }

    public synchronized void reset(long durationMillis) {
        running = false;
        remainingTime = durationMillis;
    }

    public synchronized long getTime() {
        if (running) {
            return Math.max(0, endTime - System.currentTimeMillis());
        } else {
            return remainingTime;
        }
    }

    public long getMaxTime(){return maxTime;}
    public boolean isRunning() {
        return running;
    }
}


