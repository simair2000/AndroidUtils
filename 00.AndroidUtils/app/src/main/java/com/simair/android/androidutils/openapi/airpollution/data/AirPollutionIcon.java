package com.simair.android.androidutils.openapi.airpollution.data;

import android.text.TextUtils;

import com.simair.android.androidutils.R;

public enum AirPollutionIcon {
    BEST(R.drawable.if_victory, "최고"),
    VERY_GOOD(R.drawable.if_big_smile, "좋음"),
    GOOD(R.drawable.if_happy, "양호"),
    NORMAL(R.drawable.if_secret_smile, "보통"),
    BAD(R.drawable.if_unhappy, "나쁨"),
    VERY_BAD(R.drawable.if_scorn, "상당히 나쁨"),
    WORST(R.drawable.if_amazing, "매우 나쁨"),
    VERY_WORST(R.drawable.if_anger, "최악"),
    UNKNOWN(R.drawable.if_the_iron_man, "내용없음"),
    ;

    public final int resId;
    public final String label;

    AirPollutionIcon(int resId, String label) {
        this.resId = resId;
        this.label = label;
    }

    public static AirPollutionIcon getIcon(String pm10Value) {
        if(TextUtils.isDigitsOnly(pm10Value)) {
            int value = Integer.parseInt(pm10Value);
            if(value <= 15) {
                return BEST;
            } else if(15 < value && value <= 30) {
                return VERY_GOOD;
            } else if(30 < value && value <= 40) {
                return GOOD;
            } else if(40 < value && value <= 50) {
                return NORMAL;
            } else if(50 < value && value <= 75) {
                return BAD;
            } else if(75 < value && value <= 100) {
                return VERY_BAD;
            } else if(100 < value && value <= 150) {
                return WORST;
            } else if(150 < value) {
                return VERY_WORST;
            }
        }
        return UNKNOWN;
    }
}
