/**
 * Author: Gareth Munnings
 * Created on 2025/10/03
 */

package com.example.wallstreettycoon.model;

import android.provider.ContactsContract;
import android.util.Log;

import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.stock.StockPriceFunction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketEvent implements Serializable {
    private int marketEventID;
    private int chapterID;
    private int minigameID;
    private int duration;
    private String title;
    private String info;
    private boolean isMinigame;

    private Map<Integer, Double> marketFactors;
    public MarketEvent(int marketEventID, int chapterID, int minigameID, int duration, String title, String info){
        this.marketEventID = marketEventID;
        this.chapterID = chapterID;
        this.minigameID = minigameID;
        this.duration = duration;
        this.title = title;
        this.info = info;
        marketFactors = new HashMap<>();
        this.isMinigame = (minigameID > 0);
    }
    public MarketEvent(int marketEventID, int chapterID, int minigameID, int duration, String title, String info, String marketFactors){
        this.marketEventID = marketEventID;
        this.chapterID = chapterID;
        this.minigameID = minigameID;
        this.duration = duration;
        this.title = title;
        this.info = info;
        this.marketFactors = parseToMap(marketFactors);
        this.isMinigame = (minigameID > 0);
    }

    public MarketEvent(int marketEventID, int chapterID, int minigameID, int duration, String title, String info, boolean isMinigame, Map<Integer, Double> marketFactors) {
        this.marketEventID = marketEventID;
        this.chapterID = chapterID;
        this.minigameID = minigameID;
        this.duration = duration;
        this.title = title;
        this.info = info;
        this.marketFactors = marketFactors;
        this.isMinigame = (minigameID > 0);
    }

    public int getMarketEventID() {
        return marketEventID;
    }

    public int getChapterID() {
        return chapterID;
    }

    public int getMinigameID() {
        return minigameID;
    }

    //Getter
    public String getTitle(){
        return title;
    }
    public String getInfo(){
        return info;
    }
    public int getDuration(){ return duration;}
    public boolean isMinigame() { return isMinigame;}

    //Setter
    public void setMinigame(boolean minigame) { this.isMinigame = minigame && (minigameID > 0);}

    public void applyMarketFactors(){
        Log.d("MARKET EVENT", "Applying market factors");
        for(Map.Entry<Integer, Double> entry : marketFactors.entrySet()){
            int stockID = entry.getKey();
            double factor = entry.getValue();
            Game.getInstance().getStockPriceFunction(stockID).onGameEvent(new GameEvent(GameEventType.MARKET_EVENT, "Market event", factor));
            Log.d("MARKET EVENT", "Applied factor " + factor + " to stock " + stockID);
        }
    }

    public static HashMap<Integer, Double> parseToMap(String input) {
        HashMap<Integer, Double> map = new HashMap<>();

        // Remove surrounding { } if present
        input = input.trim();
        if (input.startsWith("{") && input.endsWith("}")) {
            input = input.substring(1, input.length() - 1);
        }

        // Split by commas to get key:value pairs
        String[] pairs = input.split(",");

        for (String pair : pairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                try {
                    int key = Integer.parseInt(parts[0].trim());
                    double value = Double.parseDouble(parts[1].trim());
                    map.put(key, value);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number in pair: " + pair);
                }
            }
        }

        return map;
    }
}
