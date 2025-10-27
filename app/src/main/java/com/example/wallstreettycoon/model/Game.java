package com.example.wallstreettycoon.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.wallstreettycoon.chapter.ChapterManager;
import com.example.wallstreettycoon.chapter.ChapterState;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.stock.StockPriceFunction;
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

    private static String username;
    public static User currentUser(){
        return dbUtil.getUser(username);
    };

    public boolean gameEnded = false;
    public int currentChapterID = 0;
    public Map<Integer, ChapterState> chapterStates = new HashMap<>();
    public Set<Integer> completedMiniGames = new HashSet<>();
    public List<Integer> displayedNotifications = new ArrayList<>(); // Track displayed notification IDs
    public final List<GameEvent> pendingEvents = new ArrayList<>();
    public static Timer timer;
    private long timeStamp;
    private int currentEventIndex;
    private static Game INSTANCE;
    private static Context appContext;
    private static List<GameObserver> observers;

    public static DatabaseUtil dbUtil;
    public final static int numberOfSecondsInADay = 5;
    public List<StockPriceFunction> stockPriceFunctions;

    private Game() {

    }

    public static void initContext(Context context) {
        appContext = context.getApplicationContext();
        if (INSTANCE == null) {
            INSTANCE = new Game();
            dbUtil = DatabaseUtil.getInstance(context);
        }
    }

    public static Game getInstance() {
        if (INSTANCE == null) {
            Log.d("CONTEXT ERR", "Call initContext first");
        }
        return INSTANCE;
    }

    public static void startGame(Context context, User user) {
        Log.d("Game", "Starting game for user: " + user.getUserUsername());
        initContext(context);
        observers = new ArrayList<>();
        INSTANCE.username = user.getUserUsername();

        boolean loaded = loadFromFile(user.getUserUsername());
        if (loaded) {
            Log.d("Game", "Resuming saved game for user: " + user.getUserUsername());
            if (INSTANCE.timer == null) {
                INSTANCE.timer = new Timer(false);
            }
            INSTANCE.timer.resumeFrom(INSTANCE.timeStamp, INSTANCE.currentEventIndex + 1);
            Log.d("GAME", String.valueOf(INSTANCE.timeStamp));
        } else {
            Log.d("Game", "Starting new game for user: " + user.getUserUsername());
            INSTANCE = new Game();
            dbUtil = DatabaseUtil.getInstance(context);
            timer = new Timer();
            INSTANCE.chapterStates.put(0, ChapterState.IN_PROGRESS);
            INSTANCE.currentChapterID = 0; // Explicitly set to tutorial
            INSTANCE.displayedNotifications.clear();
            INSTANCE.stockPriceFunctions = dbUtil.getStockPriceFunctions();
        }

        addObserver(ChapterManager.getInstance());
        saveGame(); // Ensure file exists after starting
    }

    public static void pauseGame() {
        saveGame();
    }

    public static void saveGame() {
        if (timer != null) {
            INSTANCE.currentEventIndex = timer.getCurrentEventIndex();
            for(GameEvent e: getPendingEvents()){
                MarketEvent m = (MarketEvent) e.getCargo();
                m.applyMarketFactors();
            }
            INSTANCE.timeStamp = timer.getElapsedTime();
            //save market factors
        }
        saveToFile(INSTANCE.username + ".ser");
    }

    public static void saveToFile(String filename) {
        try {
            File file = new File(appContext.getFilesDir(), filename);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(INSTANCE);
            oos.close();
            Log.d("SaveFile", "Successfully saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("SaveFile", "Failed to save to file: " + filename, e);
        }
    }

    public static boolean loadFromFile(String username) {
        String filename = username + ".ser";
        File file = new File(appContext.getFilesDir(), filename);
        if (!file.exists()) {
            Log.d("LoadFile", "No save file found for user: " + username + " at " + file.getAbsolutePath());
            return false;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            INSTANCE = (Game) ois.readObject();
            ois.close();
            Log.d("LoadFile", "Loaded from file: " + file.getAbsolutePath());
            if (INSTANCE.displayedNotifications == null) {
                INSTANCE.displayedNotifications = new ArrayList<>();
            }
            return true;
        } catch (IOException | ClassNotFoundException e) {
            Log.e("LoadFile", "Failed to load from file for user: " + username, e);
            return false;
        }
    }

    public int getCurrentTimeStamp() {
        return (int) timeStamp;
    }

    public static List<GameEvent> getPendingEvents() {
        return INSTANCE.pendingEvents;
    }

    @Override
    public void onGameEvent(GameEvent event) {
        switch (event.getType()) {
            case UPDATE_STOCK_PRICE:
                timeStamp = timer.getElapsedTime();
                notifyObservers(event);
                break;
            case MARKET_EVENT:
                MarketEvent marketEvent = (MarketEvent) event.getCargo();
                if (GameStarterCloser.getCurrentActivity() instanceof ListStocks) {

                    // Add notification ID to displayedNotifications
                    displayedNotifications.add(marketEvent.getMarketEventID());
                    notifyObservers(event);
                    //give the user time to read before marketfactors are applied
                    new Handler(Looper.getMainLooper()).postDelayed(marketEvent::applyMarketFactors, 20000);
                } else {
                    pendingEvents.add(event);
                    Log.d("Game", "Queued MARKET_EVENT: " + marketEvent.getTitle() + " until ListStocks is active");
                }
                break;
            case STOCK_BOUGHT:
            case STOCK_SOLD:
                ChapterManager.getInstance().onGameEvent(event);
                break;
            case MINIGAME_COMPLETED:
                completedMiniGames.add((Integer)event.getCargo());
                ChapterManager.getInstance().onGameEvent(event);
                break;
            case CHAPTER_STARTED:
                //send first notification from chapter
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    timer.scheduleNextEvent(displayedNotifications.size());
                }, 10000L);
                break;
            case GAME_ENDED:
                gameEnded = true;
                saveGame();
                Toast toast = new Toast(getContext());
                toast.setText(event.getMessage());
                toast.show();
                notifyObservers(event);
                break;


        }
    }

    public static void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(GameEvent e) {
        for (GameObserver observer : observers) {
            observer.onGameEvent(e);
        }
    }

    public Context getContext() {
        return appContext;
    }
    public StockPriceFunction getStockPriceFunction(int stockID){
        for(StockPriceFunction spf: INSTANCE.stockPriceFunctions){
            if(spf.getStockID() == stockID){
                return spf;
            }
        }
        return null;
    }
}