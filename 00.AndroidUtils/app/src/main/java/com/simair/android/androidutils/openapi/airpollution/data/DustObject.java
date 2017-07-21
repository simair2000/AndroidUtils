package com.simair.android.androidutils.openapi.airpollution.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 21.<br />
 * 미세먼지 data
 */

public class DustObject implements Serializable {

    @SerializedName("station")          StationObject station;
    @SerializedName("timeObservation")  String dateTime;    // 측정 일시
    @SerializedName("pm10")             PM10Object pm10;

    public StationObject getStation() {
        return station;
    }

    public void setStation(StationObject station) {
        this.station = station;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public PM10Object getPm10() {
        return pm10;
    }

    public void setPm10(PM10Object pm10) {
        this.pm10 = pm10;
    }
}
