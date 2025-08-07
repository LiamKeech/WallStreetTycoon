package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

public class GameModel {
    private GameObserver gm;
    private String[] moneyWords = {"DOUGH", "STACKS", "CHEESE", "PAPER", "MOOLAH", "BANKROLL", "POCKETCHANGE"};
    private String[] stockWords = {"BULL", "BEAR", "MARGIN", "SHORT", "LONG", "STOCK", "BOND", "FUND", "INDEX", "BROKER", "TICKER", "IPO", "OPTION", "DIVIDEND", "FUTURE", "TRADER", "EQUITY", "YIELD", "SWAP", "HEDGE"};

    private Board board;
    private String currentWord;

    public GameModel(){
        this.board = new Board();
    }

    public String addLetterToCurrentWord(int[] coordinate){
        if(board.adjacentToSelectedLetter(coordinate))
            currentWord = currentWord + board.getLetter(coordinate);
        if()
        else
            currentWord = "";
        return currentWord;
    }

}
