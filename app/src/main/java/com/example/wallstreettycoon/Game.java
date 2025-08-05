package com.example.wallstreettycoon;

import android.util.Log;

import com.example.wallstreettycoon.useraccount.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Game implements java.io.Serializable {

    public static User currentUser;
    public static Timer timer;
    public static Game gameInstance = new Game();
    private static long timeStamp;
    public Game(){
    }
    public static void startGame(){
        timer = new Timer();
    }
    public static void continueGame(){
        timer.startTimer();
    }

    public static void saveGame(){
        //update elapsed time
        timeStamp = timer.getElapsedTime();
        //serialize and store in db
        saveToFile(currentUser.getUserUsername() + ".ser");
    }

    public static void saveToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(gameInstance);
            Log.d("","Saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean loadFromFile(String username) {
        String filename = username + ".ser";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            gameInstance = (Game) ois.readObject();
            Log.d("", "Loaded from file");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
