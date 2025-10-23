package com.example.wallstreettycoon.model;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

public class GameStarterCloser extends Application implements Application.ActivityLifecycleCallbacks{
    private static Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

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
