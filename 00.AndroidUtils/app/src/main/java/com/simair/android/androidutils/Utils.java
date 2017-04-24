package com.simair.android.androidutils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by simair on 16. 11. 22.
 */

public class Utils {

    public static int getRandom(int min, int max) {
        Random random = new Random();
        int ret = 0;

        ret = random.nextInt((max + 1) - min) + min;
        return ret;
    }

    public static String decimalFormat(long number, String format) {
        if(TextUtils.isEmpty(format)) {
            format = "#,###";
        }
        DecimalFormat df = new DecimalFormat(format);
        return df.format(number);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity, EditText edit) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit, 0);
    }
}
