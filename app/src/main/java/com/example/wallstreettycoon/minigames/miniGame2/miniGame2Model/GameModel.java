package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

import android.util.Log;

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

    public GameModel(){
        this.board = new Board();
        wordsFound = new ArrayList<>();
    }

    public void selectCell(int[] coordinate) {
        if(board.getSelectedCells().isEmpty()){
            //add
            board.addSelectedCell(board.getCell(coordinate));
            checkIfWordCompleted();
        }
        else{
            if(board.adjacentToSelectedLetter(coordinate)){
                //add
                board.addSelectedCell(board.getCell(coordinate));
                checkIfWordCompleted();
            }
            else{
                board.clearSelectedCells();
                //notify observer
                observer.onGameEvent(new GameEvent(GameEventType.ILLEGAL_CLICK, "Illegal click"));
                board.addSelectedCell(board.getCell(coordinate));
            }
        }
    }

    public void deselectCell(int[] coordinate){
        if(board.getSelectedCells().size() == 1)
            removeLetterFromCurrentWord();
        else{
            while(!board.getSelectedCells().getLast().equals(board.getCell(coordinate))){
                removeLetterFromCurrentWord();
            }
        }

    }
    public void removeLetterFromCurrentWord(){
        //should only remove if the coordinate is the last selected thing
        board.getSelectedCells().getLast().setDeselected();
        board.getSelectedCells().remove(board.getSelectedCells().size() - 1);
    }

    public void checkIfWordCompleted(){
        if (arrayContains(getCurrentWord(), moneyWords)) {
            wordsFound.add(getCurrentWord());
            board.setSelectedCellsFound();
            observer.onGameEvent(new GameEvent(GameEventType.WORD_FOUND, wordsFound));
            board.clearSelectedCells();
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

    public String getCurrentWord(){
        String cw = "";
        for(Cell cell: board.getSelectedCells()){
            cw = cw + cell.getLetter();
        }
        return cw;
    }

}
