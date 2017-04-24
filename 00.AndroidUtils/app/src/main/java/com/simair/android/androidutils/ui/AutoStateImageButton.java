package com.simair.android.androidutils.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Created by simair on 16. 8. 3.
 */
public class AutoStateImageButton extends ImageButton {
    public AutoStateImageButton(Context context) {
        this(context, null, 0);
    }

    public AutoStateImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoStateImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if(isEnabled() && getBackground() != null) {
                    getBackground().setColorFilter(Color.parseColor("#4c000000"), PorterDuff.Mode.SRC_ATOP);
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(getBackground() != null) {
                    getBackground().clearColorFilter();
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled) {
            setAlpha(1f);
        } else {
            setAlpha(0.3f);
        }
        super.setEnabled(enabled);
    }

}
