package com.example.wallstreettycoon;

public class Timer {
    public long startTime;
    private long elapsedTime = 0;

    public Timer(){
        startTimer();
    }

    public long getElapsedTime(){
        elapsedTime = System.nanoTime() - startTime;
        return elapsedTime;
    }

    public void startTimer(){
        startTime = System.nanoTime() + elapsedTime;
    }
}
