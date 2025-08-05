package com.example.wallstreettycoon;

public class Timer {
    public long startTime;
    private long timeStamp;

    public Timer(){
        startTimer();
    }

    public long getTimeStamp(){
        long elapsedTime = System.nanoTime() - startTime;
        return elapsedTime;
    }

    public void startTimer(){
        startTime = System.nanoTime();
    }
}
