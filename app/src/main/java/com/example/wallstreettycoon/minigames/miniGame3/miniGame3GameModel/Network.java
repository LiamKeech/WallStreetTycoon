/**
 * Author: Gareth Munnings
 * Created on 2025/08/31
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kotlin.collections.ArrayDeque;

public class Network {
    Random random = new Random();
    private int[] numNodesInEachCol = {2, 3, 4, 3, 2};
    List<List<Node>> network = new ArrayList<>();

    List<Connection> connections = new ArrayList<>();

    public Network() {
        int curColNumber = 0;
        for (int numOfNodes: numNodesInEachCol) {
            List<Node> col = new ArrayList<>();
            while(numOfNodes > 0){
                if(random.nextBoolean())
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
        if(start.colour == end.colour){
            connections.add(new Connection(start, end));
            return true;
        }
        return false;
    }

    public List<List<Node>> getCols(){
        return network;
    }

    public List<Connection> getConnections(){return connections;}
}
