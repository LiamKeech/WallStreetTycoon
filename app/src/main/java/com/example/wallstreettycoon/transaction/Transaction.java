package com.example.wallstreettycoon.transaction;

import java.math.BigDecimal;

public class Transaction {
    private long transactionID;
    private String username;
    private long stockID;
    private String transactionType; //BUY or SELL
    private int quantity;
    private BigDecimal price;
    private String transactionDate;

    public Transaction(long transactionID, String username, long stockID, String transactionType, int quantity, BigDecimal price, String transactionDate) {
        this.transactionID = transactionID;
        this.username = username;
        this.stockID = stockID;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.price = price;
        this.transactionDate = transactionDate;
    }

    public long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(long transactionID) {
        this.transactionID = transactionID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getStockID() {
        return stockID;
    }

    public void setStockID(long stockID) {
        this.stockID = stockID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}