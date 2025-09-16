/**
 * Author: Gareth Munnings
 * Created on 2025/09/16
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

public class Model {
    Timer timer;
    Network network;

    public Model(){
        network = new Network();
        timer = new Timer(10000, () -> onTimerFinish());
        timer.start();
    }

    private void onTimerFinish(){

    }

    public Network getNetwork(){return network;}
    public Timer getTimer(){return timer;}
}
