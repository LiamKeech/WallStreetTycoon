package com.example.wallstreettycoon.useraccount;

public class User { //Data model
    Integer userID; //Uniquely ID user
    String userFirstName;
    String userLastName;
    String userUsername;
    String userPassword;
    Double userBalance;

    public User(String userPassword, String userUsername, String userLastName, String userFirstName, Integer userID) {
        this.userPassword = userPassword;
        this.userUsername = userUsername;
        this.userLastName = userLastName;
        this.userFirstName = userFirstName;
        this.userID = userID;
        this.userBalance = 0.0;
    }

    //user constructor with no user id
    public User(String userPassword, String userUsername, String userLastName, String userFirstName) {
        this.userPassword = userPassword;
        this.userUsername = userUsername;
        this.userLastName = userLastName;
        this.userFirstName = userFirstName;
        this.userBalance = 0.0;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
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
