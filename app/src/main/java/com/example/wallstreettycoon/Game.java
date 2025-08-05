package com.example.wallstreettycoon;

import android.util.Log;

import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.useraccount.User;

import com.example.wallstreettycoon.Timer;

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
        Timer timer = new Timer();
    }

    public static void saveGame(){
        //update timeStamp
        timeStamp = timer.getTimeStamp();
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
    public static void loadFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            gameInstance = (Game) ois.readObject();
            Log.d("", "Loaded from file");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
