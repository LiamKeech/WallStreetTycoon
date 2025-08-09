/**
 * Author: Gareth Munnings
 * Created on 2025/08/09
 */

package com.example.wallstreettycoon.minigames.miniGame2.miniGame2Model;

import android.content.Context;
import android.util.AttributeSet;

public class SquareButton extends androidx.appcompat.widget.AppCompatButton {
    public SquareButton(Context context) {
        super(context);
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // height = width
    }
}

