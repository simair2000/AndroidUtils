package com.simair.android.androidutils.openapi.forecast.data;

/**
 * Created by simair on 17. 7. 11.
 */

public enum WindDirection {
    N1,
    NNE,
    NE,
    ENE,
    E,
    ESE,
    SE,
    SSE,
    S,
    SSW,
    SW,
    WSW,
    W,
    WNW,
    NW,
    NNW,
    N2,
    ;

    public static String getString(int code) {
        for(WindDirection item : WindDirection.values()) {
            if(item.ordinal() == code) {
                return item.name();
            }
        }
        return null;
    }
}
