package com.example.wallstreettycoon.model;

import android.content.Context;
import android.util.Log;

import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
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
    private static long timeStamp;
    private static Game INSTANCE;
    private static Context appContext;
    private static List<GameObserver> observers;

    public static DatabaseUtil dbUtil;

    private Game(){

    }

    public static void initContext(Context context){
        appContext = context.getApplicationContext();
        if(INSTANCE == null) {
            INSTANCE = new Game();
            dbUtil = DatabaseUtil.getInstance(context); // SINGLETON FIX
        }
    }

    public static Game getInstance(){
        if(INSTANCE == null){
            Log.d("CONTEXT ERR", "Call initContext first");
        }
        return INSTANCE;
    }

    //The first time the game is ever started, ie first ever login
    public static void startGame(Context context, User user){
        initContext(context);
        observers = new ArrayList<>();
        timer = new Timer();
        currentUser = user;
        saveGame();
    }

    //Every other time the game is started
//    public static void continueGame(Context context){
//        if(timer != null)
//            timer.startTimer();
//        else
//            startGame(context);
//    }

    public static void pauseGame(){
        saveGame();
    }

    private static void saveGame(){
        //update elapsed time
        timeStamp = timer.getElapsedTime();
        //serialize and store in files
        saveToFile(currentUser.getUserUsername() + ".ser");
    }

    public static void saveToFile(String filename) {
        try {
            File file = new File(appContext.getFilesDir(), filename);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(INSTANCE);
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
            File file = new File(appContext.getFilesDir(), filename);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            INSTANCE = (Game)ois.readObject();
            ois.close();
            Log.d("LoadFile", "Loaded from file: " + file.getAbsolutePath());
            return true;
        } catch (IOException | ClassNotFoundException e) {
            Log.d("", "Load from file did not work");

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
            case MARKET_EVENT:
                MarketEvent marketEvent = (MarketEvent)event.getCargo();

                marketEvent.applyMarketFactors();
                notifyObservers(event);
                break;
        }
    }

    public void addObserver(GameObserver observer){
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer){
        observers.remove(observer);
    }

    private void notifyObservers(GameEvent e){
        for(GameObserver observer : observers){
            observer.onGameEvent(e);
        }
    }
}