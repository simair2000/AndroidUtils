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
    CLOUDY_LIGHTNING(R.drawable.weather_016, R.string.lightning),


    RAIN_1(R.drawable.weather_012, R.string.rainy),
    RAIN_2(R.drawable.weather_013, R.string.rainy),
    RAIN_3(R.drawable.weather_014, R.string.rainy),

    RAIN_LIGHTNING(R.drawable.weather_015, R.string.rain_lightning),

    RAIN_SNOW_1(R.drawable.weather_023, R.string.rain_snow),

    SNOW_1(R.drawable.weather_020, R.string.snow),
    SNOW_2(R.drawable.weather_021, R.string.snow),
    SNOW_3(R.drawable.weather_022, R.string.snow),

    LIGHTNING(R.drawable.weather_029, R.string.lightning),
    ;

    public int iconRes;
    public int strRes;

    WeatherIcon(int iconRes, int strRes) {
        this.iconRes = iconRes;
        this.strRes = strRes;
    }

    /**
     * 시간당 강수량에 따른 비 아이콘 선택
     * @param hourlyPrecipitation 시간당 강수량 0 ~ 70이상
     * @return
     */
    public static WeatherIcon getRainIcon(float hourlyPrecipitation) {
        if(hourlyPrecipitation < 10) {
            return RAIN_1;
        } else if(10 <= hourlyPrecipitation && hourlyPrecipitation < 40) {
            return RAIN_2;
        }
        return RAIN_3;
    }

    /**
     * 시간당 강수량에 따른 진눈개비 아이콘 선택
     * @param hourlyPrecipitation 시간당 강수량 0 ~ 70이상
     * @return
     */
    public static WeatherIcon getRainSnowIcon(float hourlyPrecipitation) {
        // 진눈개비 아이콘은 이거 하나밖에 못구했음... ㅠㅠ
        return RAIN_SNOW_1;
    }

    /**
     * 시간당 강수량에 따른 눈 아이콘 선택
     * @param hourlyPrecipitation 시간당 강수량 0 ~ 70이상
     * @return
     */
    public static WeatherIcon getSnowIcon(float hourlyPrecipitation) {
        if(hourlyPrecipitation < 10) {
            return SNOW_1;
        } else if(10 <= hourlyPrecipitation && hourlyPrecipitation < 40) {
            return SNOW_2;
        }
        return SNOW_3;
    }
}
