package com.example.wallstreettycoon.minigames.miniGame2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.Board;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.Cell;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.ConnectionOverlay;
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
    private androidx.appcompat.widget.AppCompatButton hintButton;
    private Integer cellSize;
    FrameLayout container;
    ConnectionOverlay connectionOverlaySelectedCells;
    ConnectionOverlay connectionOverlayWordsFound;
    InsetDrawable insetDrawableButtonFound, insetDrawableButtonSelected;


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
        container = findViewById(R.id.gridContainer);
        hintButton = findViewById(R.id.hintButton);
        //end region

        int inset = 15;
        insetDrawableButtonFound = new InsetDrawable(ContextCompat.getDrawable(context, R.drawable.minigame_2_btn_found), inset, inset, inset, inset);
        insetDrawableButtonSelected = new InsetDrawable(ContextCompat.getDrawable(context, R.drawable.minigame_2_btn_selected), inset, inset, inset, inset);

        gameModel = new GameModel();
        gameModel.setObserver(this);

        drawGrid();
        initializeWordsList();
        hintButton.setOnClickListener(v -> {
            Toast toast = new Toast(this);
            toast.setText(gameModel.getHint());
            toast.show();
        });
    }

    public void drawGrid(){
        grid.removeAllViews();
        container.removeView(connectionOverlaySelectedCells);
        container.removeView(connectionOverlayWordsFound);

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

                    container.bringChildToFront(grid);
                }
            });
        }
        else{
            populateGrid(rows, cols);

            container.bringChildToFront(grid);
        }
    }
    public void populateGrid(int rows, int cols){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                SquareButton button = new SquareButton(context);
                button.setText(board.getLetter(new int[]{i, j}));
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setTypeface(ResourcesCompat.getFont(this, R.font.jua));
                button.setTextColor(Color.BLACK);
                button.setTextSize(23);

                if(board.getCell(new int[]{i,j}).isFound()){
                    button.setBackground(insetDrawableButtonFound);
                }
                if(board.getCell(new int[]{i,j}).isSelected()) {
                    button.setBackground(insetDrawableButtonSelected);
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
                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);

                grid.addView(button, params);
            }
        }

        drawSelectedCellsConnections();
        drawWordsFoundConnections();
        container.addView(connectionOverlaySelectedCells);
        container.addView(connectionOverlayWordsFound);
    }

    public void drawSelectedCellsConnections(){
        connectionOverlaySelectedCells = new ConnectionOverlay(this, R.color.LightBlue);
        ArrayList<Cell> selectedCells = (ArrayList<Cell>) gameModel.getSelectedCells();
        drawConnection(selectedCells, connectionOverlaySelectedCells);
    }

    public void drawWordsFoundConnections(){
        connectionOverlayWordsFound = new ConnectionOverlay(this, R.color.Green);
        List<List<Cell>> wordsFound = gameModel.getWordsFound();
        for(List<Cell> list: wordsFound){
            drawConnection(list, connectionOverlayWordsFound);
        }
    }

    public void drawConnection(List<Cell> list, ConnectionOverlay connectionOverlay){
        for(int i = 0; i < list.size() - 1; i++){
            Cell fromCell = list.get(i);
            Cell toCell = list.get(i+1);

            View fromButton = grid.getChildAt(fromCell.getCoordinate()[0] * 8 + fromCell.getCoordinate()[1]);
            View toButton = grid.getChildAt(toCell.getCoordinate()[0] * 8 + toCell.getCoordinate()[1]);

            connectionOverlay.connect(fromButton, toButton);
        }
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
            case GAME_OVER:
                float score = (float) gameEvent.getCargo();
                new Thread(() -> {
                    DatabaseUtil.getInstance(getApplicationContext()).uploadScores(score, "minigame2");
                }).start();
                DialogFragment endDialogFragment = new miniGame2EndDialogFragment();
                endDialogFragment.setCancelable(false);
                endDialogFragment.show(getSupportFragmentManager(), "miniGame1End");
                break;
        }
    }

    public void drawWordsList(ArrayList<String> words){
        //remove view with next number, replace with new view
        int numberOfWordsFound = words.size();

        wordListLL.removeViewAt(numberOfWordsFound - 1);
        TextView tv = new TextView(context, null, 0, R.style.LightBlueTextView);
        tv.setTextSize(18);
        tv.setPadding(30,20,30,20);
        String text = numberOfWordsFound + ": " + words.getLast();
        tv.setText(text);
        wordListLL.addView(tv, numberOfWordsFound - 1);
    }

    public void initializeWordsList(){
        int i = 1;
        while(i <= 7){
            TextView tv = new TextView(context, null, 0, R.style.LightBlueTextView);
            tv.setTextSize(18);
            tv.setPadding(30,20,30,20);
            String text = i + ":______________";
            tv.setText(text);
            wordListLL.addView(tv);
            i++;
        }
    }
}