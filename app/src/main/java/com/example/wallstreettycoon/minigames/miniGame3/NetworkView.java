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
import android.widget.Toast;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.Connection;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.Model;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.Network;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.Node;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.NodeColour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkView extends View {
    private Model model;
    private Paint nodePaint, outlinePaint, wirePaint, wireOutlinePaint;
    private Node startNode;
    private float nodeRadius = 50;
    private float dragX, dragY;

    private Map<Node, Float[]> nodePositions = new HashMap<>();

    public NetworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wirePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wireOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        wirePaint.setStrokeWidth(25);
        wireOutlinePaint.setStrokeWidth(30);

        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(6);
    }

    public void setModel(Model model){
        this.model= model;
    }

    @Override
    protected void onDraw(Canvas canvas){
        setNodePositions();
        for (Node node: nodePositions.keySet()) {
            if(node.getColour() == NodeColour.BLUE) {
                nodePaint.setColor(getResources().getColor(R.color.LightBlue));
                outlinePaint.setColor(getResources().getColor(R.color.LightBlueShadow));
            }
            else {
                nodePaint.setColor(getResources().getColor(R.color.Orange));
                outlinePaint.setColor(getResources().getColor(R.color.OrangeShadow));
            }

            Float[] pos = nodePositions.get(node);
            float x = pos[0];
            float y = pos[1];

            //fill
            canvas.drawCircle(x, y, nodeRadius, nodePaint);
            //outline
            canvas.drawCircle(x, y, nodeRadius, outlinePaint);
        }

        if (startNode != null) {
            Float[] pos = nodePositions.get(startNode);
            if(startNode.getColour() == NodeColour.BLUE)
                wirePaint.setColor(getResources().getColor(R.color.LightBlue));
            else
                wirePaint.setColor(getResources().getColor(R.color.Orange));
            canvas.drawLine(pos[0], pos[1], dragX, dragY, wirePaint);
        }

        for (Connection connection: model.network.getConnections()){
            Float[] startPos = nodePositions.get(connection.getStart());
            Float[] endPos = nodePositions.get(connection.getEnd());

            if(connection.getStart().getColour() == NodeColour.BLUE) {
                wirePaint.setColor(getResources().getColor(R.color.LightBlue));
                wireOutlinePaint.setColor(getResources().getColor(R.color.LightBlueShadow));
            }
            else {
                wirePaint.setColor(getResources().getColor(R.color.Orange));
                wireOutlinePaint.setColor(getResources().getColor(R.color.OrangeShadow));
            }


            //canvas.drawLine(startPos[0], startPos[1], endPos[0], endPos[1], wireOutlinePaint);
            canvas.drawLine(startPos[0], startPos[1], endPos[0], endPos[1], wirePaint);
        }
    }

    public void setNodePositions(){
        int colIndex = 0; // track which column we're on
        for (List<Node> col: model.network.getCols()) {
            int[] verticalPositions = getVerticalPositions(col.size(), getHeight());

            for (int rowIndex = 0; rowIndex < col.size(); rowIndex++) {
                Node node = col.get(rowIndex);

                float x = colIndex * 900 + 400 - model.network.getCurColInView() * 900;  // adjust spacing
                float y = verticalPositions[rowIndex];

                nodePositions.put(node, new Float[]{x, y});
            }
            colIndex++;
        }
    }

    public void moveToNextCol(){
        model.network.incrCurColInView();
        model.timer.addTime(10000);
        if(model.network.getCurColInView() == model.network.getCols().size() - 1) {
            //win condition
            model.onGameWin();
        }
        else
            setNodePositions();
    }

    public int[] getVerticalPositions(int count, int screenHeight) {
        int[] positions = new int[count];

        int spacing = screenHeight/5 ; // constant distance between nodes
        int blockHeight = (count - 1) * spacing;
        int startY = (screenHeight / 2) - (blockHeight / 2);

        for (int i = 0; i < count; i++) {
            positions[i] = startY + i * spacing;
        }

        return positions;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //check if user touched a node
                startNode = findNodeAtPosition(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                // draw temporary wire from startNode to finger in ondraw
                if (startNode != null) {
                    dragX = x;
                    dragY = y;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (startNode != null) {
                    Node endNode = findNodeAtPosition(x, y);
                    if (endNode != null && endNode != startNode
                            && startNode.getColour() == endNode.getColour()) {
                        //valid connection
                        if(model.network.connectNodes(startNode, endNode)){
                            moveToNextCol();
                            //add time to timer
                        }
                    }
                    startNode = null; //reset drag
                    invalidate(); //redraw the view
                }
                break;
        }

        return true;
    }

    private Node findNodeAtPosition(float touchX, float touchY) {
        for (Map.Entry<Node, Float[]> entry : nodePositions.entrySet()) {
            Float[] pos = entry.getValue();
            float dx = touchX - pos[0];
            float dy = touchY - pos[1];
            if (dx * dx + dy * dy <= nodeRadius * nodeRadius) {
                return entry.getKey();
            }
        }
        return null;
    }

}
