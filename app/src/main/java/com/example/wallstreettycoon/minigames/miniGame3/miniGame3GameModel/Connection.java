/**
 * Author: Gareth Munnings
 * Created on 2025/09/15
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

public class Connection {
    Node start;
    Node end;

    public Connection(Node start, Node end){
        this.start = start;
        this.end = end;
    }

    public Node getStart(){return start;}
    public Node getEnd(){return end;}
}
