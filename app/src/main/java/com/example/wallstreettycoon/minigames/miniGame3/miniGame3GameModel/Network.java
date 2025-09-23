/**
 * Author: Gareth Munnings
 * Created on 2025/08/31
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kotlin.collections.ArrayDeque;

public class Network {
    Random random = new Random();
    private int[] numNodesInEachCol = {3, 4, 5, 4, 2};

    private int[] seed = {1, 0, 1, 1, 0, 1, 0, 0, 1};

    private int curColInView = 0;
    List<List<Node>> network = new ArrayList<>();

    List<Connection> connections = new ArrayList<>();

    public Network() {
        int curColNumber = 0;
        for (int numOfNodes: numNodesInEachCol) {
            List<Node> col = new ArrayList<>();
            while(numOfNodes > 0){
                if(seed[numOfNodes]%(2) == 0)
                    col.add(new Node(curColNumber, numOfNodes, NodeColour.BLUE));
                else
                    col.add(new Node(curColNumber, numOfNodes, NodeColour.ORANGE));
                numOfNodes--;
            }

            network.add(col);
            curColNumber++;
        }
    }

    public boolean connectNodes(Node start, Node end){
        if(start.getColour() == end.getColour()){
            connections.add(new Connection(start, end));
            return allPossibleConnectionsFound(start.getCol(), end.getCol());
        }
        return false;
    }

    public List<List<Node>> getCols(){
        return network;
    }

    public List<Connection> getConnections(){return connections;}

    public boolean allPossibleConnectionsFound(int c1, int c2){
        List<Node> col1 = network.get(c1);
        List<Node> col2 = network.get(c2);

        for (Node n1 : col1) {
            for (Node n2 : col2) {
                //only care about nodes of the same colour
                if (n1.getColour() == n2.getColour()) {
                    boolean found = false;

                    // Check if this connection already exists
                    for (Connection conn : connections) {

                        if (conn.getStart() == n1 && conn.getEnd() == n2) {
                            found = true;
                            break;
                        }
                    }

                    // not in list
                    if (!found) return false;
                }
            }
        }
        return true;
    }

    public int getCurColInView(){return curColInView;}
    public void incrCurColInView(){
        curColInView++;

    }
}
