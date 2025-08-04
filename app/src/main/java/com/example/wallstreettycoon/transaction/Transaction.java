package com.example.wallstreettycoon.transaction;

import java.math.BigDecimal;

public class Transaction {
    private long transactionId;
    private String username;
    private String stockSymbol;
    private String type;          // buy or sell
    private int quantity;
    private BigDecimal priceAtTrade;
    private long timestamp;

    public Transaction(long transactionId, String username, String stockSymbol,
                       String type, int quantity, BigDecimal priceAtTrade, long timestamp) {
        this.transactionId = transactionId;
        this.username = username;
        this.stockSymbol = stockSymbol;
        this.type = type;
        this.quantity = quantity;
        this.priceAtTrade = priceAtTrade;
        this.timestamp = timestamp;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtTrade() {
        return priceAtTrade;
    }

    public void setPriceAtTrade(BigDecimal priceAtTrade) {
        this.priceAtTrade = priceAtTrade;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
