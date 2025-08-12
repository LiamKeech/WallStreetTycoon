package com.example.wallstreettycoon.portfolio;

import com.example.wallstreettycoon.stock.Stock;

public class PortfolioStock {
    private int portfolioID;  // FK to Portfolio
    private Stock stock;
    private int quantity;
    private double buyPrice;
    private String buyDate;

    public PortfolioStock(int portfolioID, Stock stock, int quantity, double buyPrice, String buyDate) {
        this.portfolioID = portfolioID;
        this.stock = stock;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.buyDate = buyDate;
    }

    // Getters and setters
    public int getPortfolioID() {
        return portfolioID;
    }

    public void setPortfolioID(int portfolioID) {
        this.portfolioID = portfolioID;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
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

    @Override
    public String toString() {
        return quantity + " shares of " + stock.getStockName() + " bought at " + buyPrice;
    }
}
