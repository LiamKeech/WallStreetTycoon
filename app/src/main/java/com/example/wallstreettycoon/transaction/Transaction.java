package com.example.wallstreettycoon.transaction;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction { //Data Model
    private long transactionID;
    private String username;
    private long stockID;
    private String stockSymbol;
    private String transactionType; // BUY or SELL
    private int quantity;
    private BigDecimal price;
    private Date transactionDate;

    public Transaction(long transactionID, String username, long stockID, String stockSymbol, String transactionType, int quantity, BigDecimal price, Date transactionDate) {
        this.transactionID = transactionID;
        this.username = username;
        this.stockID = stockID;
        this.stockSymbol = stockSymbol;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.price = price;
        this.transactionDate = transactionDate;
    }

    // Getters and setters
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

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    // Convenience method for adapter
    public String getType() {
        return transactionType;
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

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    // Helper method for formatted date display
    public String getFormattedDate() {
        if (transactionDate == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        return dateFormat.format(transactionDate);
    }

    // Helper method to get price as double (for adapter calculations)
    public double getPriceAsDouble() {
        return price != null ? price.doubleValue() : 0.0;
    }
}