package com.simair.android.androidutils;

import android.text.TextUtils;

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
}
