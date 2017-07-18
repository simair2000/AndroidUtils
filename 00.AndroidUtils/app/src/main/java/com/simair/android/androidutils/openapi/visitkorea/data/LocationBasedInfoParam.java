package com.simair.android.androidutils.openapi.visitkorea.data;

import com.google.gson.Gson;
import com.simair.android.androidutils.openapi.visitkorea.TourType;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 18.
 */

public class LocationBasedInfoParam implements Serializable {
    int count;
    int pageNo;
    TourType type;
    double latitude;
    double longitude;
    int range;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public TourType getType() {
        return type;
    }

    public void setType(TourType type) {
        this.type = type;
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

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
