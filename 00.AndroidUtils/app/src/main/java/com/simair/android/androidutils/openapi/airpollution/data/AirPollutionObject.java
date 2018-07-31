package com.simair.android.androidutils.openapi.airpollution.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AirPollutionObject implements Serializable {
    /**
     * Grade값
     * 1 : 좋음
     * 2 : 보통
     * 3 : 나쁨
     * 4 : 매우나쁨
     */
    @SerializedName("coGrade")      String coGrade;        // 일산화탄소 지수
    @SerializedName("coValue")      String coValue;      // 일산화탄소 농도 (단위 : ppm)
    @SerializedName("dataTime")     String dataTime;    // 오염도 측정 연-월-일 시간: 분
    @SerializedName("khaiGrade")    String khaiGrade;      // 통합대기환경지수
    @SerializedName("khaiValue")    String khaiValue;      // 통합대기환경수치
    @SerializedName("mangName")     String mangName;     // 측정망 정보 (국가배경, 교외대기, 도시대기, 도로변대기)
    @SerializedName("no2Grade")     String no2Grade;        // 이산화질소 지수
    @SerializedName("no2Value")     String no2Value;     // 이산화질소 농도 (단위 : ppm)
    @SerializedName("o3Grade")      String o3Grade;        // 오존 지수
    @SerializedName("o3Value")      String o3Value;      // 오존 농도 (단위 : ppm)
    @SerializedName("pm10Grade")    String pm10Grade;      // 미세먼지(PM10) 24시간 등급
    @SerializedName("pm10Grade1h")  String pm10Grade1h;    // 미세먼지(PM10) 1시간 등급
    @SerializedName("pm10Value")    String pm10Value;      // 미세먼지(PM10) 농도 (단위 : ㎍/㎥)
    @SerializedName("pm10Value24")  String pm10Value24;    // 미세먼지(PM10) 24시간예측이동농도 (단위 : ㎍/㎥)
    @SerializedName("pm25Grade")     String pm25Grade;     // 초미세먼지(PM2.5) 24시간 등급자료
    @SerializedName("pm25Grade1h")  String pm25Grade1h;    // 초미세먼지(PM2.5) 1시간 등급자료
    @SerializedName("pm25Value")        String pm25Value;      // 초미세먼지(PM2.5)  농도 (단위 : ㎍/㎥)
    @SerializedName("pm25Value24")      String pm25Value24;    // 미세먼지(PM2.5) 24시간예측이동농도 (단위 : ㎍/㎥)
    @SerializedName("so2Grade")         String so2Grade;       // 아황산가스 지수
    @SerializedName("so2Value")         String so2Value;     // 아황산가스 농도 (단위 : ppm)

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ AirPollution ]\n");
        sb.append("coGrade : ").append(coGrade).append("\n");
        sb.append("coValue : ").append(coValue).append("\n");
        sb.append("dataTime : ").append(dataTime).append("\n");
        sb.append("khaiGrade : ").append(khaiGrade).append("\n");
        sb.append("khaiValue : ").append(khaiValue).append("\n");
        sb.append("mangName : ").append(mangName).append("\n");
        sb.append("no2Grade : ").append(no2Grade).append("\n");
        sb.append("no2Value : ").append(no2Value).append("\n");
        sb.append("o3Grade : ").append(o3Grade).append("\n");
        sb.append("o3Value : ").append(o3Value).append("\n");
        sb.append("pm10Grade : ").append(pm10Grade).append("\n");
        sb.append("pm10Grade1h : ").append(pm10Grade1h).append("\n");
        sb.append("pm10Value : ").append(pm10Value).append("\n");
        sb.append("pm10Value24 : ").append(pm10Value24).append("\n");
        sb.append("pm25Grade : ").append(pm25Grade).append("\n");
        sb.append("pm25Grade1h : ").append(pm25Grade1h).append("\n");
        sb.append("pm25Value : ").append(pm25Value).append("\n");
        sb.append("pm25Value24 : ").append(pm25Value24).append("\n");
        sb.append("so2Grade : ").append(so2Grade).append("\n");
        sb.append("so2Value : ").append(so2Value);

        return sb.toString();
    }

    public String getCoGrade() {
        return coGrade;
    }

    public void setCoGrade(String coGrade) {
        this.coGrade = coGrade;
    }

    public String getCoValue() {
        return coValue;
    }

    public void setCoValue(String coValue) {
        this.coValue = coValue;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getKhaiGrade() {
        return khaiGrade;
    }

    public void setKhaiGrade(String khaiGrade) {
        this.khaiGrade = khaiGrade;
    }

    public String getKhaiValue() {
        return khaiValue;
    }

    public void setKhaiValue(String khaiValue) {
        this.khaiValue = khaiValue;
    }

    public String getMangName() {
        return mangName;
    }

    public void setMangName(String mangName) {
        this.mangName = mangName;
    }

    public String getNo2Grade() {
        return no2Grade;
    }

    public void setNo2Grade(String no2Grade) {
        this.no2Grade = no2Grade;
    }

    public String getNo2Value() {
        return no2Value;
    }

    public void setNo2Value(String no2Value) {
        this.no2Value = no2Value;
    }

    public String getO3Grade() {
        return o3Grade;
    }

    public void setO3Grade(String o3Grade) {
        this.o3Grade = o3Grade;
    }

    public String getO3Value() {
        return o3Value;
    }

    public void setO3Value(String o3Value) {
        this.o3Value = o3Value;
    }

    public String getPm10Grade() {
        return pm10Grade;
    }

    public void setPm10Grade(String pm10Grade) {
        this.pm10Grade = pm10Grade;
    }

    public String getPm10Grade1h() {
        return pm10Grade1h;
    }

    public void setPm10Grade1h(String pm10Grade1h) {
        this.pm10Grade1h = pm10Grade1h;
    }

    public String getPm10Value() {
        return pm10Value;
    }

    public void setPm10Value(String pm10Value) {
        this.pm10Value = pm10Value;
    }

    public String getPm10Value24() {
        return pm10Value24;
    }

    public void setPm10Value24(String pm10Value24) {
        this.pm10Value24 = pm10Value24;
    }

    public String getPm25Grade() {
        return pm25Grade;
    }

    public void setPm25Grade(String pm25Grade) {
        this.pm25Grade = pm25Grade;
    }

    public String getPm25Grade1h() {
        return pm25Grade1h;
    }

    public void setPm25Grade1h(String pm25Grade1h) {
        this.pm25Grade1h = pm25Grade1h;
    }

    public String getPm25Value() {
        return pm25Value;
    }

    public void setPm25Value(String pm25Value) {
        this.pm25Value = pm25Value;
    }

    public String getPm25Value24() {
        return pm25Value24;
    }

    public void setPm25Value24(String pm25Value24) {
        this.pm25Value24 = pm25Value24;
    }

    public String getSo2Grade() {
        return so2Grade;
    }

    public void setSo2Grade(String so2Grade) {
        this.so2Grade = so2Grade;
    }

    public String getSo2Value() {
        return so2Value;
    }

    public void setSo2Value(String so2Value) {
        this.so2Value = so2Value;
    }
}
