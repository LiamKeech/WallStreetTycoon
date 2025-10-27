package com.example.wallstreettycoon.chapter;

import android.util.Log;

import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameEventType;
import com.example.wallstreettycoon.model.GameObserver;
import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.transaction.Transaction;
import com.example.wallstreettycoon.useraccount.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChapterManager implements GameObserver {
    private static ChapterManager INSTANCE;
    private List<Chapter> chapters;

    private ChapterManager() {
        chapters = new ArrayList<>();
        loadChapters();
        syncStatesFromGame();
    }

    public static synchronized ChapterManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChapterManager();
        }
        return INSTANCE;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    private void loadChapters() {
        for (int i = 0; i <= 5; i++) { // 0: Tutorial, 1-5: Chapters
            chapters.add(new Chapter(i));
        }
    }

    private void syncStatesFromGame() {
        for (Map.Entry<Integer, ChapterState> entry : Game.getInstance().chapterStates.entrySet()) {
            int chapterId = entry.getKey();
            if (chapterId >= 0 && chapterId < chapters.size()) {
                Chapter ch = getChapter(chapterId);
                if (ch != null) {
                    ch.setState(entry.getValue());
                } else {
                    Log.w("CHAPTER MANAGER", "Null chapter for ID: " + chapterId);
                }
            } else {
                Log.w("CHAPTER MANAGER", "Invalid chapter ID in chapterStates: " + chapterId);
            }
        }
        Game.getInstance().currentChapterID = findCurrentChapterID();
    }

    private int findCurrentChapterID() {
        int highestCompleted = -1;
        for (Map.Entry<Integer, ChapterState> entry : Game.getInstance().chapterStates.entrySet()) {
            if (entry.getValue() == ChapterState.COMPLETED && entry.getKey() > highestCompleted) {
                highestCompleted = entry.getKey();
            }
        }
        int nextChapter = highestCompleted + 1;
        if (nextChapter >= chapters.size()) {
            nextChapter = chapters.size() - 1;
        }
        Log.d("CHAPTER MANAGER", "Current chapter ID set to: " + nextChapter);
        return nextChapter >= 0 ? nextChapter : 0;
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (Game.currentUser() == null || event == null) {
            Log.w("CHAPTER MANAGER", "Invalid game state or event");
            return;
        }
        GameEventType type = event.getType();
        if (type == GameEventType.STOCK_BOUGHT || type == GameEventType.STOCK_SOLD ||
                type == GameEventType.MINIGAME_COMPLETED || type == GameEventType.MARKET_EVENT) {
            checkProgression();
        }
    }

    private void checkProgression() {
        Log.d("CHAPTER MANAGER", "Checking progression");
        int currentID = Game.getInstance().currentChapterID;
        Chapter current = getChapter(currentID);
        if (current == null) {
            Log.w("CHAPTER MANAGER", "Current chapter is null for ID: " + currentID);
            return;
        }
        Log.d("CHAPTER MANAGER", "Chapter " + current.getChapterID() + " | State: " + current.getState());
        if (current.getState() != ChapterState.IN_PROGRESS) {
            Log.d("CHAPTER MANAGER", "Chapter " + currentID + " is not in progress");
            return;
        }

        if (isChapterCompleted(currentID, Game.currentUser())) {
            Log.d("CHAPTER MANAGER", "Chapter " + currentID + " completed");
            current.setState(ChapterState.COMPLETED);

            if (currentID < chapters.size() - 1) {
                int nextChapter = currentID + 1;
                Game.getInstance().currentChapterID = nextChapter;
                Chapter next = getChapter(nextChapter);
                if (next != null) {
                    next.setState(ChapterState.IN_PROGRESS);
                    Log.d("CHAPTER MANAGER", "Chapter incremented to chapter " + nextChapter);
                    Game.getInstance().onGameEvent(new GameEvent(GameEventType.CHAPTER_STARTED,
                            "Started chapter " + next.getChapterName(), next));
                } else {
                    Log.w("CHAPTER MANAGER", "Next chapter is null for ID: " + nextChapter);
                }
            } else {
                Game.getInstance().onGameEvent(new GameEvent(GameEventType.GAME_ENDED,
                        "Thank you for playing our game!", null));
                Game.getInstance().onGameEvent(new GameEvent(GameEventType.GAME_ENDED,
                        "Final balance: " + String.format("%.2f", Game.currentUser().getUserBalance()), null
                ));

            }
            Game.saveGame();
        }
    }

    private boolean isChapterCompleted(int chapterID, User user) {
        if (user == null) {
            Log.w("CHAPTER MANAGER", "User is null for chapter " + chapterID + " completion check");
            return false;
        }
        Log.d("CHAPTER MANAGER", "Checking if chapter " + chapterID + " is completed for user: " + user.getUserUsername());
        List<Transaction> txs = Game.dbUtil.getTransactionHistory(user.getUserUsername());
        Log.d("CHAPTER MANAGER", "Transaction history size: " + txs.size());
        List<PortfolioStock> portfolio = DatabaseUtil.getInstance(Game.getInstance().getContext()).getPortfolio(user.getUserUsername());
        Log.d("CHAPTER MANAGER", "Portfolio size: " + portfolio.size());

        if (!areChapterNotificationsDisplayed(chapterID)) {
            Log.d("CHAPTER MANAGER", "All notifications for chapter " + chapterID + " not yet displayed");
            return false;
        }

        switch (chapterID) {
            case 0: // Tutorial: Bought Teslo (stockID 61)
                for (Transaction tx : txs) {
                    Log.d("CHAPTER MANAGER", "Checking transaction: stockID=" + tx.getStockID() + ", type=" + tx.getTransactionType());
                    if (tx.getStockID() == 61 && "BUY".equals(tx.getTransactionType())) {
                        return true;
                    }
                }
                return false;
            case 1: // Ch1: Bought tech stocks (e.g., CRNB=1, GPLX=4), completed mini-game 1, sold tech stocks
                boolean boughtTech = false;
                boolean holdingTech = false;
                for (Transaction tx : txs) {
                    if ((tx.getStockID() == 1 ) && "BUY".equals(tx.getTransactionType())) {
                        boughtTech = true;
                    }
                }
                for (PortfolioStock ps : portfolio) {
                    if (ps.getStock().getCategory().equals("Technology")) {
                        holdingTech = true;
                        break;
                    }
                }
                boolean miniGame1Completed = Game.getInstance().completedMiniGames.contains(1);
                Log.d("CHAPTER MANAGER", "Chapter 1 - boughtTech: " + boughtTech + ", holdingTech: " + holdingTech + ", miniGame1Completed: " + miniGame1Completed);
                return boughtTech && miniGame1Completed && !holdingTech;
            case 2: // Ch2: Bought then sold banks (e.g., GDBK=16)
                boolean boughtBank = false, soldBank = false;
                for (Transaction tx : txs) {
                    if (tx.getStockID() == 16 && "BUY".equals(tx.getTransactionType())) boughtBank = true;
                    if (tx.getStockID() == 16 && "SELL".equals(tx.getTransactionType())) soldBank = true;
                }
                Log.d("CHAPTER MANAGER", "Chapter 2 - boughtBank: " + boughtBank + ", soldBank: " + soldBank);
                return boughtBank && soldBank;
            case 3: // Ch3: Sold Ch1 stock, completed puzzle (mini 2), bought crypto
                boolean soldCh1Stock = false, boughtCrypto = false, holdingCh1Stock = false;
                for (Transaction tx : txs) {
                    if ((tx.getStockID() == 1 || tx.getStockID() == 4) && "SELL".equals(tx.getTransactionType())) soldCh1Stock = true;
                    if (tx.getStockID() >= 26 && tx.getStockID() <= 35 && "BUY".equals(tx.getTransactionType())) boughtCrypto = true;
                }
                for (PortfolioStock ps : portfolio) {
                    if (ps.getStock().getStockID() == 1 || ps.getStock().getStockID() == 4) {
                        holdingCh1Stock = true;
                        break;
                    }
                }
                boolean miniGame2Completed = Game.getInstance().completedMiniGames.contains(2);
                Log.d("CHAPTER MANAGER", "Chapter 3 - soldCh1Stock: " + soldCh1Stock + ", boughtCrypto: " + boughtCrypto + ", holdingCh1Stock: " + holdingCh1Stock + ", miniGame2Completed: " + miniGame2Completed);
                return soldCh1Stock && boughtCrypto && miniGame2Completed && !holdingCh1Stock;
            case 4: // Ch4: Bought tourism (36-45), bought tech/entertainment (46-49)
                boolean boughtTourism = false;
                boughtTech = false;
                for (Transaction tx : txs) {
                    if (tx.getStockID() >= 36 && tx.getStockID() <= 45 && "BUY".equals(tx.getTransactionType())) boughtTourism = true;
                    if (tx.getStockID() >= 46 && tx.getStockID() <= 49 && "BUY".equals(tx.getTransactionType())) boughtTech = true;
                }
                Log.d("CHAPTER MANAGER", "Chapter 4 - boughtTourism: " + boughtTourism + ", boughtTech: " + boughtTech);
                return boughtTourism && boughtTech;
            case 5: // Ch5: Bought AI company (TKAI=50), completed logic mini (3)
                boolean boughtAI = false;
                for (Transaction tx : txs) {
                    if (tx.getStockID() == 50 && "BUY".equals(tx.getTransactionType())) boughtAI = true;
                }
                boolean miniGame3Completed = Game.getInstance().completedMiniGames.contains(3);

                Log.d("CHAPTER MANAGER", "Chapter 5 - boughtAI: " + boughtAI + ", miniGame3Completed: " + miniGame3Completed);
                return boughtAI && miniGame3Completed;
            default:
                Log.w("CHAPTER MANAGER", "Invalid chapter ID: " + chapterID);
                return false;
        }
    }

    private boolean areChapterNotificationsDisplayed(int chapterID) {
        List<Integer> requiredNotificationIds = getRequiredNotificationIdsForChapter(chapterID);
        boolean allDisplayed = Game.getInstance().displayedNotifications.containsAll(requiredNotificationIds);
        if (!allDisplayed) {
            Log.d("CHAPTER MANAGER", "Missing notifications for chapter " + chapterID + ": Expected " + requiredNotificationIds + ", Found " + Game.getInstance().displayedNotifications);
        }
        return allDisplayed;
    }

    public static List<Integer> getRequiredNotificationIdsForChapter(int chapterID) {
        List<Integer> requiredIds = new ArrayList<>();
        switch (chapterID) {
            case 0: // Tutorial: 8 notifications (UI intro + Teslo)
                requiredIds.add(1);  // Welcome to WallStreet Tycoon!
                requiredIds.add(2);  // Your Balance & Menu
                requiredIds.add(3);  // Market vs Portfolio
                requiredIds.add(4);  // Searching & Filtering
                requiredIds.add(5);  // Trading Stocks
                requiredIds.add(6);  // Important Notifications
                requiredIds.add(7);  // Ready for Your First Move
                requiredIds.add(8);  // A New Venture
                break;
            case 1: // Dot-Com Boom: 6 notifications (Cranberry–Market Reset)
                requiredIds.add(9);   // Cranberry Inc. Ready to Blend Innovation
                requiredIds.add(10);  // Googolplex Set to Revolutionize Online Search
                requiredIds.add(11);  // The Dot-Com Boom
                requiredIds.add(12);  // Buy More Tech Stocks
                requiredIds.add(13);  // Tech Frenzy Faces Looming Market Correction
                requiredIds.add(14);  // Market Reset Condition — Sell All Tech Holdings
                break;
            case 2: // Housing Bubble: 3 notifications
                requiredIds.add(15);  // Buy Goldbark Sachs
                requiredIds.add(16);  // Housing Market Strains
                requiredIds.add(17);  // Sell Goldbark Sachs
                break;
            case 3: // Crypto Surge: 3 notifications
                requiredIds.add(18);  // Unlock Crypto Trading
                requiredIds.add(19);  // Buy Meme Coins
                requiredIds.add(20);  // Crypto Volatility Warning
                break;
            case 4: // Corona Crash: 4 notifications
                requiredIds.add(21);  // Tourism Stocks Plunge
                requiredIds.add(22);  // Buy Tech Stocks
                requiredIds.add(23);  // Teslo Opportunity
                requiredIds.add(24);  // Tourism Stocks Recovery Opportunity
                break;
            case 5: // AI Revolution: 3 notifications
                requiredIds.add(25);  // Buy ThinkrAI
                requiredIds.add(26);  // Stabilize ThinkrAI
                break;
            default:
                Log.w("CHAPTER MANAGER", "No notifications defined for chapter ID: " + chapterID);
                break;
        }
        return requiredIds;
    }

    public Chapter getCurrentChapter() {
        return getChapter(Game.getInstance().currentChapterID);
    }

    public Chapter getChapter(int id) {
        if (id >= 0 && id < chapters.size()) {
            return chapters.get(id);
        }
        Log.w("CHAPTER MANAGER", "Invalid chapter ID requested: " + id);
        return null;
    }
}