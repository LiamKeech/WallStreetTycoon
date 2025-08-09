package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

import java.util.List;

public class Cell {
    private String letter;
    private boolean found;

    private boolean selected;

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


    public void setSelected() {
        selected = true;
    }

    public void setDeselected() {
        selected = false;
    }

    public boolean isFound(){return found;}
    public void setFound(){found = true;}

    public boolean isSelected() {
        return selected;
    }
}
