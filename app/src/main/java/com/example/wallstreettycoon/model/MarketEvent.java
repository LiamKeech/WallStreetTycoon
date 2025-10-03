/**
 * Author: Gareth Munnings
 * Created on 2025/10/03
 */

package com.example.wallstreettycoon.model;

public class MarketEvent {
    private int marketEventID;
    private int chapterID;
    private int minigameID;
    private int duration;
    private String title;
    private String info;
    public MarketEvent(int marketEventID, int chapterID, int minigameID, int duration, String title, String info){
        this.marketEventID = marketEventID;
        this.chapterID = chapterID;
        this.minigameID = minigameID;
        this.duration = duration;
        this.title = title;
        this.info = info;
    }
    public String getTitle(){
        return title;
    }
    public String getInfo(){
        return info;
    }
    public int getDuration(){return duration;}
}
