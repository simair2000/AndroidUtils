package com.simair.android.androidutils.openapi.forecast.data;

import com.simair.android.androidutils.R;

/**
 * Created by simair on 17. 7. 13.
 */

public enum WeatherIcon {

    SUNNY(R.drawable.weather_001, R.string.sunny),
    CLOUDY_1(R.drawable.weather_003, R.string.cloudy_1),
    CLOUDY_2(R.drawable.weather_004, R.string.cloudy_2),
    CLOUDY_3(R.drawable.weather_011, R.string.cloudy_3),
    RAINY(R.drawable.weather_013, R.string.rainy),
    RAIN_SNOW(R.drawable.weather_023, R.string.rain_snow),
    SNOW(R.drawable.weather_025, R.string.snow),
    LIGHTNING(R.drawable.weather_029, R.string.lightning),
    ;

    public int iconRes;
    public int strRes;

    WeatherIcon(int iconRes, int strRes) {
        this.iconRes = iconRes;
        this.strRes = strRes;
    }
}
