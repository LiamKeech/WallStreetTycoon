package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

public class Cell {
    private String letter;
    private boolean found;

    private int[] coordinate;

    public Cell(String letter, int[] coordinate){
        this.letter = letter;
        this.coordinate = coordinate;
        found = false;
    }

    public String getLetter(){
        return letter;
    }
    public int[] getCoordinate(){return coordinate;}
}
