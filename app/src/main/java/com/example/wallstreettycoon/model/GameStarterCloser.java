package com.example.wallstreettycoon.model;

import android.app.Application;

public class GameStarterCloser extends Application {
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Game.pauseGame();
        }
    }
    @Override
    public void onTerminate(){
        super.onTerminate();
        if (Game.getInstance() != null) {
            Game.pauseGame();
        }
    }
}
