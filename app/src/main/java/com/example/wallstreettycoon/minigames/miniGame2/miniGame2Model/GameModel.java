package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

import android.util.Log;

import com.example.wallstreettycoon.minigames.miniGame2.miniGame2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    //TODO make current word just the selected words concatonated
    private GameObserver observer;
    private String[] moneyWords = {"DOUGH", "STACKS", "CHEESE", "PAPER", "MOOLAH", "BANKROLL", "POCKETCHANGE"};
    private String[] stockWords = {"BULL", "BEAR", "MARGIN", "SHORT", "LONG", "STOCK", "BOND", "FUND", "INDEX", "BROKER", "TICKER", "IPO", "OPTION", "DIVIDEND", "FUTURE", "TRADER", "EQUITY", "YIELD", "SWAP", "HEDGE"};

    private List<String> wordsFound;
    private Board board;
    private String currentWord = "";

    public GameModel(){
        this.board = new Board();
        wordsFound = new ArrayList<>();
    }

    public void selectCell(int[] coordinate) {
        board.getCell(coordinate).setSelected();
        board.addSelectedCell(board.getCell(coordinate));
        addLetterToCurrentWord(coordinate);


    }

    public void deselectCell(int[] coordinate){
        removeLetterFromCurrentWord(coordinate);
        board.getCell(coordinate).setDeselected();
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
    public void removeLetterFromCurrentWord(int[] coordinate){
        //should only remove if the coordinate is the last selected thing

    }

    public void checkIfWordCompleted(){
        if (arrayContains(currentWord, moneyWords)) {
            wordsFound.add(currentWord);
            board.setSelectedCellsFound();
            observer.onGameEvent(new GameEvent(GameEventType.WORD_FOUND, currentWord));

            currentWord = "";
        }
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

    public List<Cell> getSelectedCells(){
        return board.getSelectedCells();
    }

    public Board getBoard(){return board;}

}
