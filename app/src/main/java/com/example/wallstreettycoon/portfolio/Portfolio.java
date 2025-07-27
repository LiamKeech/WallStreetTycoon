package com.example.wallstreettycoon.portfolio;

import com.example.wallstreettycoon.useraccount.User;

public class Portfolio { //Data Model
    private int portfolioID;
    private String ownerUsername; //FK
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.ownerUsername = user.getUserUsername();
        }
    }
}
