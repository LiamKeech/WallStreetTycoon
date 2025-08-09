package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private String[][] gridLetters = {
            {"S", "E", "H", "G", "O", "D", "O", "M"},
            {"E", "E", "B", "U", "L", "O", "R", "E"},
            {"C", "H", "A", "H", "A", "P", "A", "P"},
            {"A", "C", "N", "K", "N", "G", "E", "P"},
            {"T", "K", "L", "R", "H", "A", "O", "C"},
            {"S", "S", "L", "O", "C", "T", "E", "K"},

    };

    private Cell[][] board;

    private Cell selectedCell;
    private List<Cell> selectedCells;

    public Board(){
        board = new Cell[6][8];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = new Cell(gridLetters[i][j], new int[]{i,j});
            }
        }
        selectedCells = new ArrayList<>();
    }

    public String getLetter(int[] coordinate){
        int x = coordinate[0];
        int y = coordinate[1];
        return board[x][y].getLetter();
    }

    public boolean adjacentToSelectedLetter(int[] coordinate){
        if(selectedCell == null)
            return true;

        int[] selectedCellCoordinate = selectedCell.getCoordinate();
        int row = coordinate[0];
        int col = coordinate[1];
        int sRow = selectedCellCoordinate[0];
        int sCol = selectedCellCoordinate[1];

        if(Math.abs(row - sRow) <= 1 && Math.abs(col - sCol) <= 1 && !coordinate.equals(selectedCellCoordinate)) {
            return true;
        }

        return false;
    }

    public int getNumRows(){
        return board.length;
    }

    public int getNumCols(){
        return board[0].length;
    }

    public Cell getCell(int[] coordinate) {
        return board[coordinate[0]][coordinate[1]];
    }
    public List<Cell> getSelectedCells() {
        return selectedCells;
    }

    public void addSelectedCell(Cell cell){
        selectedCells.add(cell);
    }

    public void setSelectedCellsFound(){
        for(Cell cell: selectedCells){
            cell.setFound();
        }
    }
}
