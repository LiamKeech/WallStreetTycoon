/**
 * Author: Gareth Munnings
 * Created on 2025/09/15
 */

package com.example.wallstreettycoon.minigames.miniGame3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.Network;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.Node;

import java.util.List;

public class NetworkView extends View {
    private Network network;
    private Paint nodePaint, wirePaint;
    private Node startNode;

    public NetworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wirePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wirePaint.setStrokeWidth(8);
    }

    public void setNetwork(Network network){
        this.network = network;
    }

    @Override
    protected void onDraw(Canvas canvas){
        int colIndex = 0; // track which column we're on
        for (List<Node> col: network.getCols()) {
            int[] verticalPositions = getVerticalPositions(col.size(), getHeight());

            for (int rowIndex = 0; rowIndex < col.size(); rowIndex++) {
                Node node = col.get(rowIndex);

                // X depends on column index
                float x = colIndex * 900 + 700;  // adjust spacing as needed
                // Y depends on row index
                float y = verticalPositions[rowIndex];

                canvas.drawCircle(x, y, 50, nodePaint);
            }

            colIndex++;
        }

    }

    public int[] getVerticalPositions(int count, int screenHeight) {
        int[] positions = new int[count];

        int spacing = screenHeight/4 ; // constant distance between nodes
        int blockHeight = (count - 1) * spacing;
        int startY = (screenHeight / 2) - (blockHeight / 2);

        for (int i = 0; i < count; i++) {
            positions[i] = startY + i * spacing;
        }

        return positions;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Detect start node, move, and snap to end node
        // Only call network.connectNodes(start, end) to update model
        return true;
    }


}
