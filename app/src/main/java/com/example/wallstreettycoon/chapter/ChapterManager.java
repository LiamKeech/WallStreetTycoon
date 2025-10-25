package com.example.wallstreettycoon.chapter;

import android.util.Log;

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameEventType;
import com.example.wallstreettycoon.model.GameObserver;
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

    private void loadChapters() {
        for (int i = 0; i <= 5; i++) { // 0: Tutorial, 1-5: Chapters
            chapters.add(new Chapter(i));
        }
    }

    private void syncStatesFromGame() {
        for (Map.Entry<Integer, ChapterState> entry : Game.getInstance().chapterStates.entrySet()) {
            Chapter ch = getChapter(entry.getKey());
            if (ch != null) {
                ch.setState(entry.getValue());
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
        return nextChapter >= 0 ? nextChapter : 0;
    }

    @Override
    public void onGameEvent(GameEvent event) {
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
        Log.d("CHAPTER MANAGER", current.getChapterID() + " | " + current.getState());
        if (current == null || current.getState() != ChapterState.IN_PROGRESS) {
            return;
        }

        if (isChapterCompleted(currentID, Game.currentUser)) {
            Log.d("CHAPTER MANAGER", "Chapter " + currentID + " completed");
            current.setState(ChapterState.COMPLETED);
            if (currentID < 5) {
                int currentChapterID = Game.getInstance().currentChapterID;
                int nextChapter = currentChapterID + 1;
                Game.getInstance().currentChapterID = nextChapter;
                Log.d("CHAPTER MANAGER", "Chapter incremented to chapter " + nextChapter);
                Chapter next = getChapter(Game.getInstance().currentChapterID);
                next.setState(ChapterState.IN_PROGRESS);
                Game.getInstance().onGameEvent(new GameEvent(GameEventType.CHAPTER_STARTED,
                        "Started chapter " + next.getChapterName(), next));
            } else {
                Game.getInstance().onGameEvent(new GameEvent(GameEventType.GAME_ENDED,
                        "Game ended. Final balance: " + Game.currentUser.getUserBalance(), null));
            }
            Game.saveGame();
        }
    }

    private boolean isChapterCompleted(int chapterID, User user) {
        Log.d("CHAPTER MANAGER", "Checking if chapter " + chapterID + " is completed");
        List<Transaction> txs = Game.dbUtil.getTransactionHistory(user.getUserUsername());
        boolean boughtTech = false;

        if (!areChapterNotificationsDisplayed(chapterID)) {
            Log.d("CHAPTER MANAGER", "All notifications for chapter " + chapterID + " not yet displayed");
            return false;
        }

        switch (chapterID) {
            case 0: // Tutorial: Bought Teslo (stockID 61)
                for (Transaction tx : txs) {
                    Log.d("CHAPTER MANAGER", "Checking transaction: " + tx.getStockID());
                    if (tx.getStockID() == 61 && "BUY".equals(tx.getTransactionType())) {
                        return true;
                    }
                }
                return false;
            case 1: // Ch1: Bought tech stocks (e.g., CRNB=1, GPLX=4), completed mini-game 1, sold tech stocks
                boughtTech = false;
                boolean soldTech = false;
                for (Transaction tx : txs) {
                    if ((tx.getStockID() == 1 || tx.getStockID() == 4) && "BUY".equals(tx.getTransactionType())) {
                        boughtTech = true;
                    }
                    if ((tx.getStockID() == 1 || tx.getStockID() == 4) && "SELL".equals(tx.getTransactionType())){
                        soldTech = true;
                    }
                }
                return boughtTech && Game.getInstance().completedMiniGames.contains(1) && soldTech;
            case 2: // Ch2: Bought then sold banks (e.g., GDBK=16)
                boolean boughtBank = false, soldBank = false;
                for (Transaction tx : txs) {
                    if (tx.getStockID() == 16 && "BUY".equals(tx.getTransactionType())) boughtBank = true;
                    if (tx.getStockID() == 16 && "SELL".equals(tx.getTransactionType())) soldBank = true;
                }
                return boughtBank && soldBank;
            case 3: // Ch3: Sold Ch1 stock, completed puzzle (mini 2), bought crypto
                boolean soldCh1Stock = false, boughtCrypto = false;
                for (Transaction tx : txs) {
                    if ((tx.getStockID() == 1 || tx.getStockID() == 4) && "SELL".equals(tx.getTransactionType())) soldCh1Stock = true;
                    if (tx.getStockID() >= 26 && tx.getStockID() <= 35 && "BUY".equals(tx.getTransactionType())) boughtCrypto = true;
                }
                return soldCh1Stock && boughtCrypto && Game.getInstance().completedMiniGames.contains(2);
            case 4: // Ch4: Bought tourism (36-45), bought tech/entertainment (46-49)
                boolean boughtTourism = false;
                boughtTech = false;
                for (Transaction tx : txs) {
                    if (tx.getStockID() >= 36 && tx.getStockID() <= 45 && "BUY".equals(tx.getTransactionType())) boughtTourism = true;
                    if (tx.getStockID() >= 46 && tx.getStockID() <= 49 && "BUY".equals(tx.getTransactionType())) boughtTech = true;
                }
                return boughtTourism && boughtTech;
            case 5: // Ch5: Bought AI company (TKAI=50), completed logic mini (3)
                boolean boughtAI = false;
                for (Transaction tx : txs) {
                    if (tx.getStockID() == 50 && "BUY".equals(tx.getTransactionType())) boughtAI = true;
                }
                return boughtAI && Game.getInstance().completedMiniGames.contains(3);
            default:
                return false;
        }
    }

    private boolean areChapterNotificationsDisplayed(int chapterID) {
        List<Integer> requiredNotificationIds = getRequiredNotificationIdsForChapter(chapterID);
        return Game.getInstance().displayedNotifications.containsAll(requiredNotificationIds);
    }

    public static List<Integer> getRequiredNotificationIdsForChapter(int chapterID) {
        List<Integer> requiredIds = new ArrayList<>();
        switch (chapterID) {
            case 0: // Tutorial: 1 notification
                requiredIds.add(1);
                break;
            case 1: // Chapter 1: 5 notifications
                requiredIds.add(2);
                requiredIds.add(3);
                requiredIds.add(4);
                requiredIds.add(5);
                requiredIds.add(6);
                break;
            case 2: // Chapter 2: 2 notifications
                requiredIds.add(7);
                requiredIds.add(8);
                break;
            case 3: // Chapter 3: 3 notifications
                requiredIds.add(9);
                requiredIds.add(10);
                requiredIds.add(11);
                break;
            case 4: // Chapter 4: 4 notifications
                requiredIds.add(12);
                requiredIds.add(13);
                requiredIds.add(14);
                requiredIds.add(15);
                break;
            case 5: // Chapter 5: 2 notifications
                requiredIds.add(16);
                requiredIds.add(17);
                break;
            default:
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
        return null;
    }
}