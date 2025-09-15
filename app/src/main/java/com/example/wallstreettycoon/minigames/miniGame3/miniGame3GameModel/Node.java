/**
 * Author: Gareth Munnings
 * Created on 2025/09/15
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

public class Node {
    int col, row;
    boolean connected;

    NodeColour colour;

    public Node(int col, int row, NodeColour colour) {
        this.col = col;
        this.row = row;
        this.connected = false;
        this.colour = colour;
    }

    public NodeColour getColour(){
        return colour;
    }

    public int getCol(){return col;}
    public int getRow(){return row;}
}

