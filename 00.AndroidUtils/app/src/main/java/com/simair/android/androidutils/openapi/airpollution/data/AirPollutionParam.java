package com.simair.android.androidutils.openapi.airpollution.data;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by simair on 17. 7. 21.
 */

public class AirPollutionParam implements Serializable {
    double latitude;
    double longitude;
    String dateTime;

    public AirPollutionParam() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        dateTime = sdf.format(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
