package com.simair.android.androidutils.openapi.forecast.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 11.
 */

public class ForecastObject implements Serializable {
    @SerializedName("baseDate")         String baseDate;    // 20170711
    @SerializedName("baseTime")         String baseTime;    // 1500
    @SerializedName("category")         String category;
    @SerializedName("nx")               int x;              // UTMK X
    @SerializedName("ny")               int y;              // UTMK Y
    @SerializedName("obsrValue")        String value;       // 측정값

    @SerializedName("fcstDate")         String forecastDate; // 예측 일
    @SerializedName("fcstTime")         String forecastTime; // 예측 시간
    @SerializedName("fcstValue")        String forecastValue;    // 예측값

    public String getForecastDateTime() {
        return forecastDate + forecastTime;
    }


    public String getBaseDate() {
        return baseDate;
    }

    public void setBaseDate(String baseDate) {
        this.baseDate = baseDate;
    }

    public String getBaseTime() {
        return baseTime;
    }

    public void setBaseTime(String baseTime) {
        this.baseTime = baseTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(String forcastDate) {
        this.forecastDate = forcastDate;
    }

    public String getForecastTime() {
        return forecastTime;
    }

    public void setForecastTime(String forcastTime) {
        this.forecastTime = forcastTime;
    }

    public String getForecastValue() {
        return forecastValue;
    }

    public void setForecastValue(String forcastValue) {
        this.forecastValue = forcastValue;
    }
}
