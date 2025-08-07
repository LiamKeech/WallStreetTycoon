package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

import com.example.wallstreettycoon.minigames.miniGame2.miniGame2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private GameObserver observer;
    private String[] moneyWords = {"DOUGH", "STACKS", "CHEESE", "PAPER", "MOOLAH", "BANKROLL", "POCKETCHANGE"};
    private String[] stockWords = {"BULL", "BEAR", "MARGIN", "SHORT", "LONG", "STOCK", "BOND", "FUND", "INDEX", "BROKER", "TICKER", "IPO", "OPTION", "DIVIDEND", "FUTURE", "TRADER", "EQUITY", "YIELD", "SWAP", "HEDGE"};

    private List<String> wordsFound;
    private Board board;
    private String currentWord;

    public GameModel(){
        this.board = new Board();
        wordsFound = new ArrayList<>();
    }

    public String addLetterToCurrentWord(int[] coordinate){
        if(board.adjacentToSelectedLetter(coordinate)) {
            currentWord = currentWord + board.getLetter(coordinate);
            checkIfWordCompleted();
        }
        else
            currentWord = "";
        return currentWord;
    }

    public boolean checkIfWordCompleted(){
        if (arrayContains(currentWord, moneyWords)) {
            observer.onGameEvent(new GameEvent(GameEventType.WORD_FOUND, currentWord));
        }
        return false;
    }

    public boolean arrayContains(String x, String[] array){
        for(String s: array){
            if(s.equals(x)) return true;
        }
        return false;
    }

    public void setObserver(GameObserver obsever){
        this.observer = obsever;
    }
}
