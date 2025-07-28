package com.example.wallstreettycoon.portfolio;

import com.example.wallstreettycoon.stock.Stock;

public class PortfolioStock { //Data Model
    private int portfolioID;
    private String stockID;
    private int quantity;
    private double buyPrice;
    private String buyDate;
    private Stock stock;

    public PortfolioStock(int portfolioID, String stockID, int quantity, double buyPrice, String buyDate, Stock stock) {
        this.portfolioID = portfolioID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.buyDate = buyDate;
        this.stock = stock;
    }

    public int getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(int portfolioID) {
        this.portfolioID = portfolioID;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}