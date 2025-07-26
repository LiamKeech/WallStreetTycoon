package com.example.wallstreettycoon.useraccount;

public class User { //Data model
    String username; //Uniquely ID user
    String userFirstName;
    String userLastName;

    String userPassword;
    Double userBalance = 0.0;

    public User(String username, String userFirstName, String userLastName, String userPassword, Double userBalance) {
        this.userPassword = userPassword;
        this.username = username;
        this.userLastName = userLastName;
        this.userFirstName = userFirstName;
        this.userBalance = userBalance;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserUsername() {
        return username;
    }

    public void setUsername(String userUsername) {
        this.username = userUsername;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Double getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(Double userBalance) {
        this.userBalance = userBalance;
    }
}
