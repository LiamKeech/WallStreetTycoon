package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

public class GameEvent {

    private GameEventType type;
    private Object cargo;

    public GameEvent(GameEventType type, Object cargo) {
        this.type = type;
        this.cargo = cargo;
    }

    public GameEventType getType() { return type; }
    public Object getCargo() { return cargo; }
}
