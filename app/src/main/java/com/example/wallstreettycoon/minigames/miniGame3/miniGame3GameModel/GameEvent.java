/**
 * Author: Gareth Munnings
 * Created on 2025/09/22
 */

package com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel;

public class GameEvent {
    private GameEventType type;
    private String message;
    private Object cargo;

    public GameEvent(GameEventType type, String message, Object cargo) {
        this.type = type;
        this.message = message;
        this.cargo = cargo;
    }

    public GameEventType getType() { return type; }
    public String getMessage() { return message; }
    public Object getCargo() { return cargo; }
}
