/**
 * Author: Gareth Munnings
 * Created on 2025/09/16
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

import android.widget.Toast;

public class Model {
    public Timer timer;
    public Network network;

    private GameObserver gameObserver;

    public Model(){
        network = new Network();
        timer = new Timer(20000, () -> onTimerFinish());
        timer.start();
    }

    private void onTimerFinish(){
        GameEvent finish = new GameEvent(GameEventType.GAME_OVER, "Game Over", false);
        gameObserver.onGameEvent(finish);
    }

    public Network getNetwork(){return network;}
    public Timer getTimer(){return timer;}

    public void setGameOvserver(GameObserver go){this.gameObserver = go;}
}
