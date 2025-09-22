/**
 * Author: Gareth Munnings
 * Created on 2025/09/16
 */

package com.example.wallstreettycoon.minigames.miniGame3;

import android.content.AttributionSource;
import android.content.Context;
import android.graphics.Canvas;
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
    public void onDraw(Canvas canvas){

        Paint paint = new TextPaint();
        paint.setColor(getResources().getColor(R. color. Green));
        paint.setTextSize(50);
        //canvas.drawText(time, 100, 100, paint);

        Rect rect = new Rect(100,  1000 - (int) timer.getTime()/20 , 400, 1000);
        canvas.drawRect(rect, paint);
    }
}
