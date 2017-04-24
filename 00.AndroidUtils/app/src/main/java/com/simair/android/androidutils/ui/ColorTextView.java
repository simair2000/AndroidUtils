package com.simair.android.androidutils.ui;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by simair on 2016-03-16.
 */
public class ColorTextView extends TextView {
    private SpannableString spannableString;

    public ColorTextView(Context context) {
        this(context, null, 0);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        spannableString = new SpannableString(getText().toString());
    }

    public class Part {
        String subString;
        int color;
    }

    /**
     * 각 Part의 패턴마다 각각의 color값을 따로 준다.
     * @param parts
     */
    public void setTextColor(Part ... parts) {
        if(TextUtils.isEmpty(getText()) || parts == null || parts.length <= 0) {
            return;
        }

        String text = getText().toString();
        for(Part part : parts) {
            if(text.indexOf(part.subString) != -1) {
                setTextColor(part.subString, part.color);
            }
        }

    }

    /**
     * TextView의 모든 subString의 color값을 지정한다
     * @param subString
     * @param color
     */
    public void setTextColor(String subString, int color) {
        if(TextUtils.isEmpty(getText()) || TextUtils.isEmpty(subString)) {
            return;
        }

        String text = getText().toString();

        int start = 0;
        int end = 0;
        for(; start != -1; start = end) {
            ForegroundColorSpan fColor = new ForegroundColorSpan(color);
            start = text.indexOf(subString, start);
            if(start == -1) {
                break;
            }
            end = start + subString.length();
            spannableString.setSpan(fColor, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(spannableString);
    }

    public void setBGColor(String subString, int color) {
        if(TextUtils.isEmpty(getText()) || TextUtils.isEmpty(subString)) {
            return;
        }

        String text = getText().toString();

        int start = 0;
        int end = 0;
        for(; start != -1; start = end) {
            BackgroundColorSpan bColor = new BackgroundColorSpan(color);
            start = text.indexOf(subString, start);
            if(start == -1) {
                break;
            }
            end = start + subString.length();
            spannableString.setSpan(bColor, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(spannableString);
    }

    public void removeAllColor() {
        Spannable text = getEditableText();
        if(!TextUtils.isEmpty(text)) {
            Object[] spans = text.getSpans(0, text.length(), Object.class);
            for(Object span : spans) {
                if(span instanceof BackgroundColorSpan || span instanceof ForegroundColorSpan) {
                    text.removeSpan(span);
                }
            }
        }
    }
}
