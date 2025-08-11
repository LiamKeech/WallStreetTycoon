/**
 * Author: Gareth Munnings
 * Created on 2025/08/11
 */

package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.wallstreettycoon.R;

import java.util.ArrayList;
import java.util.List;

public class ConnectionOverlay extends View {
    private Paint paint;
    private List<Pair<View, View>> connections = new ArrayList<>(); //all the pairs that need connecting

    public ConnectionOverlay(Context context, int color) {
        super(context);
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, color));
        paint.setStrokeWidth(30f);
        paint.setAntiAlias(true);
    }

    public void connect(View start, View end) {
        connections.add(new Pair<>(start, end));
        invalidate(); //redraw the connection
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Pair<View, View> pair : connections) {
            View v1 = pair.first;
            View v2 = pair.second;

            float x1 = v1.getX() + v1.getWidth() / 2f;
            float y1 = v1.getY() + v1.getHeight() / 2f;
            float x2 = v2.getX() + v2.getWidth() / 2f;
            float y2 = v2.getY() + v2.getHeight() / 2f;

            canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }
}
