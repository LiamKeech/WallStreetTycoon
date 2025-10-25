package com.example.wallstreettycoon.chapter;

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.stock.Stock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chapter {
    private int chapterID;
    private String chapterName;
    private String description;
    private ChapterState state;
    private List<Stock> chapterStocks;

    public Chapter(int chapterID) {
        this.chapterID = chapterID;
        this.state = ChapterState.NOT_STARTED;
        this.chapterStocks = new ArrayList<>();
        loadData();
        loadChapterStocks();
    }

    private void loadData() {
        Map<String, String> data = Game.dbUtil.getChapterData(chapterID);
        this.chapterName = data.getOrDefault("chapterName", "");
        this.description = data.getOrDefault("description", "");
    }

    private void loadChapterStocks() {
        this.chapterStocks = Game.dbUtil.getChapterStocks(chapterID);
    }

    public int getChapterID() {
        return chapterID;
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getDescription() {
        return description;
    }

    public ChapterState getState() {
        return state;
    }

    public void setState(ChapterState state) {
        this.state = state;
        // Sync to Game for save
        Game.getInstance().chapterStates.put(chapterID, state);
    }

    public List<Stock> getChapterStocks() {
        return chapterStocks;
    }
}