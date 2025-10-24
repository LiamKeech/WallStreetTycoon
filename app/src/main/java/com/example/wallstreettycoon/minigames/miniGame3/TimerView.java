/**
 * Author: Gareth Munnings
 * Created on 2025/09/16
 */

package com.example.wallstreettycoon.minigames.miniGame3;

import android.content.AttributionSource;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3GameModel.Timer;

public class TimerView extends View {
    Timer timer;
    String time = "";

    private Runnable updater;
    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTimer(Timer timer){
        this.timer=timer;
        updateTimer();
    }

    public void updateTimer(){
        updater = new Runnable() {
            @Override
            public void run(){
                time = String.valueOf(timer.getTime());
                invalidate();
                postDelayed(this, 10);
            }
        };
        post(updater);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        float progress = Math.min(1f, Math.max(0f, (float) timer.getTime() / timer.getMaxTime()));

        float left = width * 0.15f;
        float right = width * 0.85f;
        float bottom = height;
        float top = bottom - (height * 0.95f * progress);

        Rect rect = new Rect((int) left, (int) top, (int) right, (int) bottom);

        if(progress > 0.7f)
        {
            drawRect(rect, getResources().getColor(R.color.Green), getResources().getColor(R.color.GreenShadow), canvas);
        }
        else if (progress > 0.4f) {
            drawRect(rect, getResources().getColor(R.color.Yellow), getResources().getColor(R.color.YellowShadow), canvas);
        }
        else{
            drawRect(rect, getResources().getColor(R.color.Red), getResources().getColor(R.color.RedShadow), canvas);
        }

    }

    private void drawRect(Rect rect, int color, int shadowColor, Canvas canvas){
        Paint paint = new Paint();
        //filled rect
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRect(rect, paint);

        //outline
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setColor(shadowColor);
        canvas.drawRect(rect, paint);
    }

}
