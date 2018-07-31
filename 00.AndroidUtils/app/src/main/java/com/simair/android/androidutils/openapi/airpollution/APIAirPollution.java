package com.simair.android.androidutils.openapi.airpollution;

import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;
import com.simair.android.androidutils.openapi.airpollution.data.AirPollutionObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;

/**
 * Created by simair on 17. 7. 20.
 * 한국 환경공단 국가 대기오염 정보 OPEN API<br />
 */
public class APIAirPollution {

    private static final String serviceKey = "rA5V0ir6KO4pTACUyKzsmTM4NuXzqTHgTCk4iVeKGHbq5zBbEsJWNQMau6rZVIgyJubgAlIjwbxppIsFWmUlIA==";
    private static final String protocol = "http";
    private static final String host = "openapi.airkorea.or.kr";
    private static final String path = "/openapi/services/rest/MsrstnInfoInqireSvc";
    private static final String pathAirPollution = "/openapi/services/rest/ArpltnInforInqireSvc";
    private static final String hostPath = host + path;
    private static final String hostPathAirPollution = host + pathAirPollution;

    private static final String APIGetTMStdrCrdnt = "getTMStdrCrdnt";
    private static final String APIGetNearbyStationName = "getNearbyMsrstnList";
    private static final String APIGetAirPollutionInfoByStationName = "getMsrstnAcctoRltmMesureDnsty";

    private static Properties getParam() {
        Properties params = new Properties();
        params.setProperty("ServiceKey", serviceKey);
        params.setProperty("_returnType", "json");
        return params;
    }

    /**
     * TM 기준 좌표정보를 얻어온다
     * @param keyword 읍/면/동 이름
     * @return
     * @throws NetworkException
     * @throws JSONException
     * @throws Exception
     */
    public static Pair<Double, Double> getTMCoord(String keyword, String admin, String locality) throws NetworkException, JSONException, Exception {
        Properties params = getParam();
        keyword = keyword.replaceAll("[0-9]","");
        params.setProperty("umdName", keyword);
        params.setProperty("pageNo", "1");
        params.setProperty("numOfRows", "10");

        Properties header = new Properties();
        header.setProperty("Connection", "Keep-Alive");
        String strResponse = Network.get(protocol, hostPath, APIGetTMStdrCrdnt, header, params);
        if(!TextUtils.isEmpty(strResponse)) {
            JSONObject json = new JSONObject(strResponse);
            if(json.has("list")) {
                JSONArray list = json.getJSONArray("list");
                if(list != null && list.length() > 0) {
                    if(list.length() == 1) {
                        double tmX = list.getJSONObject(0).getDouble("tmX");
                        double tmY = list.getJSONObject(0).getDouble("tmY");
                        return new Pair<Double, Double>(tmX, tmY);
                    }
                    for(int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);
                        String sidoName = item.getString("sidoName");
                        String sggName = item.getString("sggName");
                        if(!TextUtils.isEmpty(sidoName) /*&& !TextUtils.isEmpty(sggName)*/) {
                            if(sidoName.equalsIgnoreCase(admin) /*&& sggName.equalsIgnoreCase(locality)*/) {
                                double tmX = item.getDouble("tmX");
                                double tmY = item.getDouble("tmY");
                                return new Pair<Double, Double>(tmX, tmY);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getNearbyStationName(double tmX, double tmY) throws NetworkException, JSONException, Exception {
        Properties params = getParam();
        params.setProperty("tmX", String.valueOf(tmX));
        params.setProperty("tmY", String.valueOf(tmY));
        Properties header = new Properties();
        header.setProperty("Connection", "Keep-Alive");
        String strResponse = Network.get(protocol, hostPath, APIGetNearbyStationName, header, params);
        if(!TextUtils.isEmpty(strResponse)) {
            JSONObject json = new JSONObject(strResponse);
            JSONArray list = json.getJSONArray("list");
            if(list != null && list.length() > 0) {
                JSONObject item = list.getJSONObject(0);
                return item.getString("stationName");
            }
        }
        return null;
    }

    public static AirPollutionObject getAirPollutionInfoByStationName(String stationName) throws Exception {
        Properties params = getParam();
        params.setProperty("numOfRows", "3");
        params.setProperty("pageNo", "1");
        params.setProperty("stationName", stationName);
        params.setProperty("dataTerm", "DAILY");
        params.setProperty("ver", "1.3");
        Properties header = new Properties();
        header.setProperty("Connection", "Keep-Alive");
        String strResponse = Network.get(protocol, hostPathAirPollution, APIGetAirPollutionInfoByStationName, header, params);
        if(!TextUtils.isEmpty(strResponse)) {
            JSONObject json = new JSONObject(strResponse);
            JSONArray list = json.getJSONArray("list");
            if(list != null && list.length() > 0) {
                JSONObject item = list.getJSONObject(0);
                return new Gson().fromJson(item.toString(), AirPollutionObject.class);
            }
        }
        return null;
    }
}
