package com.example.wallstreettycoon.minigames.miniGame2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wallstreettycoon.R;

public class miniGame2 extends AppCompatActivity {
    private Board board;
    private GridLayout grid;
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

        //link entities
         grid = findViewById(R.id.grid);

        //end region
        drawGrid();
    }

    public void drawGrid(){
        board = new Board();

        grid.setRowCount(board.getNumRows());
        grid.setColumnCount(board.getNumCols());

        grid.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int totalWidth = grid.getWidth();
            int totalHeight = grid.getHeight();

            int cellSize = Math.min(totalWidth / board.getNumCols(), totalHeight / board.getNumRows());

            for (int i = 0; i < board.getNumRows(); i++) {
                for (int j = 0; j < board.getNumCols(); j++) {
                    Button button = new Button(this);
                    button.setText(board.getLetter(i, j));

                    button.setBackgroundColor(Color.TRANSPARENT);
                    button.setTextColor(Color.BLACK);
                    button.setTextSize(20);

                    button.setPadding(0, 0, 0, 0);
                    button.setMinWidth(0);
                    button.setMinHeight(0);
                    button.setMinimumWidth(0);
                    button.setMinimumHeight(0);

                    button.setOnClickListener(v -> {
                        //handle button click
                    });

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i),GridLayout.spec(j));

                    params.width = cellSize;
                    params.height = cellSize;
                    params.setMargins(0,0,0,0);

                    button.setLayoutParams(params);

                    grid.addView(button);
                }
            }
        });
    }
}