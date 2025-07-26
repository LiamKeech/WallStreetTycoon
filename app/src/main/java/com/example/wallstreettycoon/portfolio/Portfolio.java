package com.example.wallstreettycoon.portfolio;

public class Portfolio { //Data Model
    private int portfolioID;
    private String ownerUsername; //FK

    //TODO: total portfolio value

    public Portfolio(int portfolioID, String ownerUsername) {
        this.portfolioID = portfolioID;
        this.ownerUsername = ownerUsername;
    }

    public int getPortfolioID() {
        return portfolioID;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
}
