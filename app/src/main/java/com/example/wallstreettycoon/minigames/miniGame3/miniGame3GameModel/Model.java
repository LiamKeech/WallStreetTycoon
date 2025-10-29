/**
 * Author: Gareth Munnings
 * Created on 2025/09/16
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

public class Model {
    public Timer timer;
    public Network network;
    private GameObserver gameObserver;

    public Model(){
        network = new Network();
        timer = new Timer(30000, () -> onTimerFinish());
        timer.start();
    }

    private void onTimerFinish(){
        GameEvent finish = new GameEvent(GameEventType.GAME_OVER, "Game Over", false);
        gameObserver.onGameEvent(finish);
    }

    public Network getNetwork(){return network;}
    public Timer getTimer(){return timer;}



    public void setGameObserver(GameObserver go){this.gameObserver = go;}

    public void onGameWin(){
        gameObserver.onGameEvent(new GameEvent(GameEventType.GAME_OVER, "Game Over", true));
        timer.stop();
    }
}
