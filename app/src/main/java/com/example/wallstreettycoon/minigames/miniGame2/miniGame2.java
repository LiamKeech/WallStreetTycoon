package com.example.wallstreettycoon.minigames.miniGame2;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
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
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.GameEvent;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.GameModel;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model.GameObserver;

public class miniGame2 extends AppCompatActivity implements GameObserver {
    private GameModel gameModel;
    private Board board;
    private GridLayout grid;
    private LinearLayout wordListLL;

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

        drawGrid();
    }

    public void drawGrid(){
        board = new Board();

        grid.setRowCount(board.getNumRows());
        grid.setColumnCount(board.getNumCols());

        grid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                grid.getViewTreeObserver().removeOnGlobalLayoutListener(this); // remove listener to avoid multiple calls

                int totalWidth = grid.getWidth();
                int totalHeight = grid.getHeight();

                int margin = 20;
                int cellSize = Math.min(totalWidth / board.getNumCols(), totalHeight / board.getNumRows());
                cellSize -= 2 * margin;

                for (int i = 0; i < board.getNumRows(); i++) {
                    for (int j = 0; j < board.getNumCols(); j++) {
                        Button button = new Button(context);
                        button.setText(board.getLetter(new int[]{i,j}));

                        button.setBackgroundColor(Color.TRANSPARENT);
                        button.setTextColor(Color.BLACK);
                        button.setTextSize(20);

                        button.setPadding(0, 0, 0, 0);
                        button.setMinWidth(0);
                        button.setMinHeight(0);
                        button.setMinimumWidth(0);
                        button.setMinimumHeight(0);

                        int row = i;
                        int col = j;
                        button.setOnClickListener(v -> {
                            button.setBackground(ContextCompat.getDrawable(context, R.drawable.minigame_2_btn_selected));
                            gameModel.addLetterToCurrentWord(new int[]{row,col});
                        });

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
                        params.width = cellSize;
                        params.height = cellSize;
                        params.setMargins(margin, margin, margin, margin);

                        button.setLayoutParams(params);

                        grid.addView(button);
                    }
                }
            }
        });

    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        switch(gameEvent.getType()){
            case WORD_FOUND:
                //write word to the list of words
                TextView tv = new TextView(context);
                tv.setText((String)gameEvent.getCargo());
                wordListLL.addView(new TextView(context));

                Log.d("",(String)gameEvent.getCargo());

                //make cells different color
                break;

        }
    }
}