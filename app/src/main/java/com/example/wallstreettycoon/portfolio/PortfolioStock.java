package com.example.wallstreettycoon.portfolio;

public class PortfolioStock { //Data Model
    private int portfolioStockID;
    private int portfolioID;       // FK to Portfolio
    private String stockID;    // FK to Stock
    private int quantity;
    private double buyPrice;
    private String buyDate;

    public PortfolioStock(int portfolioStockID, int portfolioID, String stockID, int quantity, double buyPrice, String buyDate) {
        this.portfolioStockID = portfolioStockID;
        this.portfolioID = portfolioID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.buyDate = buyDate;
    }

    public int getPortfolioStockID() {
        return portfolioStockID;
    }

    public void setPortfolioStockID(int portfolioStockID) {
        this.portfolioStockID = portfolioStockID;
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
}
