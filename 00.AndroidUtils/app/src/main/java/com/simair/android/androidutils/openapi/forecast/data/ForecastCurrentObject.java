package com.simair.android.androidutils.openapi.forecast.data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 11.<br />
 * 초단기실황 데이터
 */

public class ForecastCurrentObject implements Serializable {
    @SerializedName("T1H")      float degree;                 // 기온 [℃]
    @SerializedName("RN1")      float hourlyPrecipitation;    // 시간당 강수량 [mm]
    @SerializedName("SKY")      int sky;                    // 맑음(1), 구름조금(2), 구름많음(3), 흐림(4)
    @SerializedName("UUU")      float EWWind;                 // 동서바람성분 [m/s] 동풍(+표기), 서풍(-표기)
    @SerializedName("VVV")      float SNWind;                 // 남북바람성분 [m/s] 북풍(+표기), 남풍(-표기)
    @SerializedName("REH")      int humidity;               // 습도 [%]
    @SerializedName("PTY")      int precipitationType;      // 강수형태 [없음(0), 비(1), 비/눈,진눈개비(2), 눈(3)]
    @SerializedName("LGT")      int thunderbolt;            // 낙뢰 [없음(0), 있음(1)]
    @SerializedName("VEC")      int windDirection;          // 풍향
    @SerializedName("WSD")      float windSpeed;              // 풍속
    private String baseDate;
    private String baseTime;

    public void addItem(ForecastObject item) {
        addItem(item, item.getValue());
    }

    public void addItem(ForecastObject item, String value) {
        baseDate = item.getBaseDate();
        baseTime = item.getBaseTime();
        if(item.getCategory().equals("T1H")) {
            setDegree(Float.valueOf(value));
        } else if(item.getCategory().equals("RN1")) {
            setHourlyPrecipitation(Float.valueOf(value));
        } else if(item.getCategory().equals("SKY")) {
            setSky(Integer.valueOf(value));
        } else if(item.getCategory().equals("UUU")) {
            setEWWind(Float.valueOf(value));
        } else if(item.getCategory().equals("VVV")) {
            setSNWind(Float.valueOf(value));
        } else if(item.getCategory().equals("REH")) {
            setHumidity(Integer.valueOf(value));
        } else if(item.getCategory().equals("PTY")) {
            setPrecipitationType(Integer.valueOf(value));
        } else if(item.getCategory().equals("LGT")) {
            setThunderbolt(Integer.valueOf(value));
        } else if(item.getCategory().equals("VEC")) {
            setWindDirection(Integer.valueOf(value));
        } else if(item.getCategory().equals("WSD")) {
            setWindSpeed(Float.valueOf(value));
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
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

    public float getDegree() {
        return degree;
    }

    public void setDegree(float degree) {
        this.degree = degree;
    }

    public float getHourlyPrecipitation() {
        return hourlyPrecipitation;
    }

    public void setHourlyPrecipitation(float hourlyPrecipitation) {
        this.hourlyPrecipitation = hourlyPrecipitation;
    }

    public int getSky() {
        return sky;
    }

    public void setSky(int sky) {
        this.sky = sky;
    }

    public float getEWWind() {
        return EWWind;
    }

    public void setEWWind(float EWWind) {
        this.EWWind = EWWind;
    }

    public float getSNWind() {
        return SNWind;
    }

    public void setSNWind(float SNWind) {
        this.SNWind = SNWind;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPrecipitationType() {
        return precipitationType;
    }

    public void setPrecipitationType(int precipitationType) {
        this.precipitationType = precipitationType;
    }

    public int getThunderbolt() {
        return thunderbolt;
    }

    public void setThunderbolt(int thunderbolt) {
        this.thunderbolt = thunderbolt;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }
}
