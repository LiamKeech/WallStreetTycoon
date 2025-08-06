package com.example.wallstreettycoon.minigames.miniGame2;

public class Board {
    private String[] moneyWords = {"DOUGH", "STACKS", "CHEESE", "PAPER", "MOOLAH", "BANKROLL", "POCKETCHANGE"};
    private String[] stockWords = {"BULL", "BEAR", "MARGIN", "SHORT", "LONG", "STOCK", "BOND", "FUND", "INDEX", "BROKER", "TICKER", "IPO", "OPTION", "DIVIDEND", "FUTURE", "TRADER", "EQUITY", "YIELD", "SWAP", "HEDGE"};

    private String[][] gridLetters = {
            {"S", "E", "M", "G", "O", "D", "M"},
            {"E", "E", "B", "U", "L", "O", "R"},
            {"C", "H", "A", "H", "A", "P", "P"},
            {"A", "C", "N", "K", "N", "G", "E"},
            {"T", "L", "L", "R", "H", "A", "O"},
            {"S", "S", "L", "O", "C", "I", "F"},
            {"S", "L", "O", "C", "I", "F", "K"}
    };

    private Cell[][] board;

    public Board(){
        board = new Cell[6][8];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = new Cell(gridLetters[i][j]);
            }
        }
    }

}
