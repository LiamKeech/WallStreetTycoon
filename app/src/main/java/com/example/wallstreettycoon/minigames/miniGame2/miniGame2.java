package com.example.wallstreettycoon.minigames.miniGame2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wallstreettycoon.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.Board;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.Cell;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.GameEvent;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.GameModel;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.GameObserver;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.SquareButton;

import java.util.ArrayList;
import java.util.List;

public class miniGame2 extends AppCompatActivity implements GameObserver {
    private GameModel gameModel;
    private Board board;
    private GridLayout grid;
    private LinearLayout wordListLL;
    private Integer cellSize;

    //TODO draw lines between cells when selected
    //TODO check for game over

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mini_game2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        context = this;

        //link entities
        grid = findViewById(R.id.grid);
        wordListLL = findViewById(R.id.wordListLL);

        //end region

        gameModel = new GameModel();
        gameModel.setObserver(this);

        drawWordsList(new ArrayList<>());
        drawGrid();

    }

    public void drawGrid(){
        grid.removeAllViews();
        board = gameModel.getBoard();
        int rows = board.getNumRows();
        int cols = board.getNumCols();

        grid.setRowCount(rows);
        grid.setColumnCount(cols);

        if (cellSize == null) {
            grid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    grid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int totalHeight = grid.getHeight();
                    cellSize = totalHeight / (rows + 1);
                    populateGrid(rows, cols);
                    drawConnections();
                }
            });
        }
        else{
            populateGrid(rows, cols);
            drawConnections();
        }
    }
    public void populateGrid(int rows, int cols){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                SquareButton button = new SquareButton(context);
                button.setText(board.getLetter(new int[]{i, j}));
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setTextColor(Color.BLACK);
                button.setTextSize(23);

                if(board.getCell(new int[]{i,j}).isFound()){
                    int inset = 25;
                    InsetDrawable insetDrawable = new InsetDrawable(ContextCompat.getDrawable(context, R.drawable.minigame_2_btn_found), inset, inset, inset, inset);
                    button.setBackground(insetDrawable);
                }
                if(board.getCell(new int[]{i,j}).isSelected()) {
                    int inset = 25;
                    InsetDrawable insetDrawable = new InsetDrawable(ContextCompat.getDrawable(context, R.drawable.minigame_2_btn_selected), inset, inset, inset, inset);
                    button.setBackground(insetDrawable);
                }
                int row = i;
                int col = j;
                button.setOnClickListener(v -> {
                    if(!board.getCell(new int[]{row, col}).isSelected()){
                        gameModel.selectCell(new int[]{row, col});
                        drawGrid();
                    }
                    else{
                        button.setBackground(null);
                        gameModel.deselectCell(new int[]{row,col});
                        drawGrid();
                    }

                    Log.d("", gameModel.getCurrentWord());

                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);

                grid.addView(button, params);
            }
        }
    }

    public void drawConnections(){
        //draw the currently selected cells connections
        ArrayList<Cell> selectedCells = (ArrayList<Cell>) gameModel.getSelectedCells();
        for(int i = 0; i < selectedCells.size() - 1; i++){
            Cell fromCell = selectedCells.get(i);
            Cell toCell = selectedCells.get(i+1);

            View fromButton = grid.getChildAt(fromCell.getCoordinate()[0] * fromCell.getCoordinate()[1]);
            View toButton = grid.getChildAt(toCell.getCoordinate()[0] * toCell.getCoordinate()[1]);
        }


        //draw found words connections
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        switch(gameEvent.getType()){
            case WORD_FOUND:
                //write word to the list of words
                drawWordsList((ArrayList<String>) gameEvent.getCargo());
                drawGrid();
                break;
            case ILLEGAL_CLICK:
                drawGrid();
                break;
        }
    }

    public void drawWordsList(ArrayList<String> words){
        wordListLL.removeAllViews();
        int i = 1;
        for(String word: words) {
            TextView tv = new TextView(context, null, 0, R.style.text);
            String text = i + ": " + word;
            tv.setText(text);
            wordListLL.addView(tv);
            i++;
        }
        while(i <= 7){
            TextView tv = new TextView(context, null, 0, R.style.text);
            String text = i + ":______________";
            tv.setText(text);
            wordListLL.addView(tv);
            i++;
        }
    }
}