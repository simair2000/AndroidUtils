package com.simair.android.androidutils.openapi.airpollution.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 21.<br />
 * 관측소 Object
 */

public class StationObject implements Serializable {
    @SerializedName("longitude")    double longitude;
    @SerializedName("latitude")     double latitude;
    @SerializedName("name")         String name;    // 관측소 이름
    @SerializedName("id")           int id;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
