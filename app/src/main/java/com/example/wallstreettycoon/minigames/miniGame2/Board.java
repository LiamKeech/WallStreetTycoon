package com.example.wallstreettycoon.minigames.miniGame2;

public class Board {
    private String[] moneyWords = {"DOUGH", "STACKS", "CHEESE", "PAPER", "MOOLAH", "BANKROLL", "POCKETCHANGE"};
    private String[] stockWords = {"BULL", "BEAR", "MARGIN", "SHORT", "LONG", "STOCK", "BOND", "FUND", "INDEX", "BROKER", "TICKER", "IPO", "OPTION", "DIVIDEND", "FUTURE", "TRADER", "EQUITY", "YIELD", "SWAP", "HEDGE"};

    private String[][] gridLetters = {
            {"S", "E", "H", "G", "O", "D", "O", "M"},
            {"E", "E", "B", "U", "L", "O", "R", "E"},
            {"C", "H", "A", "H", "A", "P", "A", "P"},
            {"A", "C", "N", "K", "N", "G", "E", "P"},
            {"T", "K", "L", "R", "H", "A", "O", "C"},
            {"S", "S", "L", "O", "C", "T", "E", "K"},

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

    public String getLetter(int x, int y){
        return board[x][y].getLetter();
    }

    public int getNumRows(){
        return board.length;
    }

    public int getNumCols(){
        return board[0].length;
    }

}
