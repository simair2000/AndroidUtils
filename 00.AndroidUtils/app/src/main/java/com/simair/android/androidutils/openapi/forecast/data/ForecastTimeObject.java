package com.simair.android.androidutils.openapi.forecast.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by simair on 17. 7. 17.
 */

public class ForecastTimeObject implements Serializable {
    @SerializedName("fcstDate")     String forecastDate;    // 예측일
    @SerializedName("fcstTime")     String forecastTime;    // 예측시

    ForecastCurrentObject data;

    public ForecastTimeObject() {
        data = new ForecastCurrentObject();
    }

    public String getDateTime() {
        return forecastDate + forecastTime;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(String forecastDate) {
        this.forecastDate = forecastDate;
    }

    public String getForecastTime() {
        return forecastTime;
    }

    public void setForecastTime(String forecastTime) {
        this.forecastTime = forecastTime;
    }

    public ForecastCurrentObject getData() {
        return data;
    }

    public void setData(ForecastCurrentObject data) {
        this.data = data;
    }

    public static void addData(HashMap<String, ForecastTimeObject> map, ForecastObject data) {
        ForecastTimeObject item = map.get(data.getForecastDateTime());
        if(item == null) {
            item = new ForecastTimeObject();
            map.put(data.getForecastDateTime(), item);
        }
        item.setForecastDate(data.getForecastDate());
        item.setForecastTime(data.getForecastTime());
        item.getData().addItem(data, data.getForecastValue());
    }
}
