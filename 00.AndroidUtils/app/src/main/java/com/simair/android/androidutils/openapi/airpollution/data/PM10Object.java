package com.simair.android.androidutils.openapi.airpollution.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 21.<br />
 * 미세먼지 수치
 */

public class PM10Object implements Serializable {
    @SerializedName("grade")        String grade;
    @SerializedName("value")        float value;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
