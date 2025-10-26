package com.example.wallstreettycoon.stock;

import java.util.ArrayList;
import java.util.List;

public class Stock {
    private int stockID;
    private String stockName;
    private String symbol;
    private String category;
    private String description;
    private Double initialPrice;

    // Constructor with all parameters
    public Stock(int stockID, String stockName, String symbol, String category, String description, Double initialPrice){
        this.stockID = stockID;
        this.stockName = stockName;
        this.symbol = symbol;
        this.category = category;
        this.description = description;
        this.initialPrice = initialPrice;
    }

    // Constructor with only stockID (creates null stock if needed)
    public Stock(int stockID){
        this.stockID = stockID;
    }

    // Getter and Setter for stockID
    public Integer getStockID() {
        return stockID;
    }

    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    // Getter and Setter for stockName
    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    // Getter and Setter for symbol
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    // Getter and Setter for category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Getter and Setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return stockName;
    }
    public Double getInitialPrice(){return initialPrice;}
}