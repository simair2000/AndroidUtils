package com.simair.android.androidutils.openapi.forecast.data;

import com.simair.android.androidutils.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by simair on 17. 7. 13.
 */

public enum WeatherIcon {

    SUNNY(R.drawable.sun, R.drawable.sunny_night, R.string.sunny),
    CLOUDY_1(R.drawable.cloud1, R.drawable.cloudy1_night, R.string.cloudy_1),
    CLOUDY_2(R.drawable.cloudy3, R.drawable.cloudy3_night, R.string.cloudy_2),
    CLOUDY_3(R.drawable.cloudy4, R.drawable.cloudy4_night, R.string.cloudy_3),
    CLOUDY_LIGHTNING(R.drawable.tstorm3, R.drawable.tstorm3, R.string.lightning),


    RAIN_1(R.drawable.shower1, R.drawable.shower1_night, R.string.rainy),
    RAIN_2(R.drawable.shower2, R.drawable.shower2_night, R.string.rainy),
    RAIN_3(R.drawable.shower3, R.drawable.shower3, R.string.rainy),

    RAIN_LIGHTNING(R.drawable.tstorm2, R.drawable.tstorm2_night, R.string.rain_lightning),

    RAIN_SNOW_1(R.drawable.sleet, R.drawable.sleet, R.string.rain_snow),

    SNOW_1(R.drawable.snow1, R.drawable.snow1_night, R.string.snow),
    SNOW_2(R.drawable.snow2, R.drawable.snow2_night, R.string.snow),
    SNOW_3(R.drawable.snow3, R.drawable.snow3_night, R.string.snow),

    LIGHTNING(R.drawable.thunder, R.drawable.thunder, R.string.lightning),
    ;

    public int iconRes;
    public int iconResNight;
    public int strRes;

    WeatherIcon(int iconRes, int iconResNight, int strRes) {
        this.iconRes = iconRes;
        this.iconResNight = iconResNight;
        this.strRes = strRes;
    }

    public int getTimedIcon(int hour) {
        if(hour < 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            hour = Integer.parseInt(sdf.format(new Date()));
        }
        if(6 < hour && hour <= 18) {
            return iconRes;
        }
        return iconResNight;
    }

    /**
     * 시간당 강수량에 따른 비 아이콘 선택
     * @param hourlyPrecipitation 시간당 강수량 0 ~ 70이상
     * @return
     */
    public static WeatherIcon getRainIcon(float hourlyPrecipitation) {
        if(hourlyPrecipitation < 5) {
            return RAIN_1;
        } else if(5 <= hourlyPrecipitation && hourlyPrecipitation < 15) {
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
