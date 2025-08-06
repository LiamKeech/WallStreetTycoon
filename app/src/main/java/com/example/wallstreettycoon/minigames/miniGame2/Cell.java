package com.example.wallstreettycoon.minigames.miniGame2;

public class Cell {
    private String letter;
    private boolean found;

    public Cell(String letter){
        this.letter = letter;
        found = false;
    }

    public String getLetter(){
        return letter;
    }
}
