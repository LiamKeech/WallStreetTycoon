package com.example.wallstreettycoon.model;

import android.content.Context;
import android.util.Log;

import com.example.wallstreettycoon.chapter.ChapterManager;
import com.example.wallstreettycoon.chapter.ChapterState;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.useraccount.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Game implements GameObserver, java.io.Serializable {

    public static User currentUser;

    //Chapter fields
    public static int currentChapterID = 0;
    public static Map<Integer, ChapterState> chapterStates = new HashMap<>();
    public static Set<Integer> completedMiniGames = new HashSet<>();

    public static Timer timer;
    private static long timeStamp;
    private static Game INSTANCE;
    private static Context appContext;
    private static List<GameObserver> observers;

    public static DatabaseUtil dbUtil;
    public static final List<GameEvent> pendingEvents = new ArrayList<>();

    private Game(){

    }

    public static void initContext(Context context){
        appContext = context.getApplicationContext();
        if(INSTANCE == null) {
            INSTANCE = new Game();
            dbUtil = DatabaseUtil.getInstance(context);
        }

    }

    public static Game getInstance(){
        if(INSTANCE == null){
            Log.d("CONTEXT ERR", "Call initContext first");
        }
        return INSTANCE;
    }

    public static void startGame(Context context, User user){
        initContext(context);
        observers = new ArrayList<>();
        timer = new Timer();
        currentUser = user;
        saveGame();

        chapterStates.put(0, ChapterState.IN_PROGRESS); // Start tutorial
        addObserver(ChapterManager.getInstance());
    }

    public static void pauseGame(){
        saveGame();
    }

    public static void saveGame(){
        timeStamp = timer.getElapsedTime();
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
    public static List<GameEvent> getPendingEvents(){return pendingEvents;}

    @Override
    public void onGameEvent(GameEvent event) {
        switch(event.getType()){
            case UPDATE_STOCK_PRICE:
                timeStamp = timer.getElapsedTime();

                // Update price history for all stocks
                updateAllStockPriceHistories();

                notifyObservers(event);
                break;
            case MARKET_EVENT:
                if (GameStarterCloser.getCurrentActivity() instanceof ListStocks) {
                    MarketEvent marketEvent = (MarketEvent) event.getCargo();
                    marketEvent.applyMarketFactors();
                    notifyObservers(event);
                } else {
                    pendingEvents.add(event);
                    MarketEvent marketEvent = (MarketEvent)event.getCargo();
                    Log.d("Game", "Queued MARKET_EVENT: " + marketEvent.getTitle() + " until ListStocks is active");
                }
                break;
        }
    }

    private void updateAllStockPriceHistories() {
        // Get all stock IDs from database
        List<Integer> stockIDs = dbUtil.getAllStockIDs();

        for (Integer stockID : stockIDs) {
            dbUtil.updateStockPriceHistory(stockID);
        }

        Log.d("Game", "Updated price history for " + stockIDs.size() + " stocks at timestamp " + timeStamp);
    }

    public static  void addObserver(GameObserver observer){
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