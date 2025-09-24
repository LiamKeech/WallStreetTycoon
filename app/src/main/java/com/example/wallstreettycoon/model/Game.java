package com.example.wallstreettycoon.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.wallstreettycoon.useraccount.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Game implements GameObserver, java.io.Serializable {

    public static User currentUser;
    public static int currentChapter = 1;
    public static Timer timer;
    public static Game gameInstance;
    private static long timeStamp;
    private static Context context;

    private static List<GameObserver> observers;
    public Game(Context context){
        this.context = context;
    }
    public static void startGame(){
        gameInstance = new Game(context);
        observers = new ArrayList<>();
        timer = new Timer();
    }
    public static void continueGame(){
        if(timer != null)
            timer.startTimer();
        else
            startGame();
    }

    public static void saveGame(){
        //update elapsed time
        timeStamp = timer.getElapsedTime();
        //serialize and store in db
        saveToFile(currentUser.getUserUsername() + ".ser");
    }

    public static void saveToFile(String filename) {
        try {
            File file = new File(context.getFilesDir(), filename);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(gameInstance);
            oos.close();
            Log.d("SaveFile", "Saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.d("", "save to file did not work");
            e.printStackTrace();
        }
    }
    public static boolean loadFromFile(String username) {
        String filename = username + ".ser";
        try {
            File file = new File(context.getFilesDir(), filename);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            gameInstance = (Game)ois.readObject();
            ois.close();
            Log.d("LoadFile", "Loaded from file: " + file.getAbsolutePath());
            return true;
        } catch (IOException | ClassNotFoundException e) {
            Log.d("", "Load from file did not work");
            e.printStackTrace();
            return false;
        }
    }

    public int getCurrentTimeStamp(){
        return (int)timeStamp;
    }


    @Override
    public void onGameEvent(GameEvent event) {
        switch(event.getType()){
            case UPDATE_STOCK_PRICE:
                timeStamp = timer.getElapsedTime();
                notifyObservers(event);
                break;
        }
    }

    public void addObserver(GameObserver observer){
        observers.add(observer);
    }

    private void notifyObservers(GameEvent e){
        for(GameObserver observer : observers){
            observer.onGameEvent(e);
        }
    }
}
