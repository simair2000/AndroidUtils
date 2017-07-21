package com.simair.android.androidutils.openapi.forecast;

import com.google.gson.Gson;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;
import com.simair.android.androidutils.openapi.forecast.data.ForecastCurrentObject;
import com.simair.android.androidutils.openapi.forecast.data.ForecastObject;
import com.simair.android.androidutils.openapi.forecast.data.ForecastTimeObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by simair on 17. 7. 11.<br />
 * 날씨 예보 OPEN API <a href="http://www.data.go.kr/dataset/15000099/openapi.do">http://www.data.go.kr/dataset/15000099/openapi.do</a><br />
 * serviceKey 는 simair2000 계정으로 받은 개발 키
 */

public class APIForecast {

    private static final String serviceKey = "rA5V0ir6KO4pTACUyKzsmTM4NuXzqTHgTCk4iVeKGHbq5zBbEsJWNQMau6rZVIgyJubgAlIjwbxppIsFWmUlIA==";
    private static final String protocol = "http";
    private static final String host = "newsky2.kma.go.kr";
    private static final String path = "/service/SecndSrtpdFrcstInfoService2";
    private static final String hostPath = host + path;

    private static final String APIForecastGrib = "ForecastGrib";
    private static final String APIForecastTimeData = "ForecastTimeData";

    /**
     * 앞으로 몇시간 날씨 조회
     * @param x coord X
     * @param y coord Y
     * @return
     * @throws NetworkException
     * @throws JSONException
     * @throws Exception
     */
    public static HashMap<String, ForecastTimeObject> requestTimeData(float x, float y) throws NetworkException, JSONException, Exception {
        Properties params = getParam();
        params.setProperty("nx", String.valueOf((int)x));
        params.setProperty("ny", String.valueOf((int)y));

        params.setProperty("base_date", getTodayDate());
        params.setProperty("base_time", getCurrentTime());
        params.setProperty("numOfRows", "1000");

        String strResponse = Network.get(protocol, hostPath, APIForecastTimeData, null, params);
        JSONObject response = new JSONObject(strResponse).getJSONObject("response");
        JSONObject header = response.getJSONObject("header");
        if(header.getString("resultCode").equals("0000") && header.getString("resultMsg").equals("OK")) {
            JSONObject body = response.getJSONObject("body");
            JSONArray items = body.getJSONObject("items").getJSONArray("item");
            HashMap<String, ForecastTimeObject> map = new HashMap<>();
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                ForecastObject data = new Gson().fromJson(item.toString(), ForecastObject.class);
                ForecastTimeObject.addData(map, data);
            }
            return map;
        }
        return null;
    }

    /**
     * 현재날씨 조회 (초단기실황조회)<br />
     * ex)<br />
     * {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},"body":{"items":{"item":[{"baseDate":20170711,"baseTime":1500,"category":"LGT","nx":60,"ny":122,"obsrValue":0},{"baseDate":20170711,"baseTime":1500,"category":"PTY","nx":60,"ny":122,"obsrValue":0},{"baseDate":20170711,"baseTime":1500,"category":"REH","nx":60,"ny":122,"obsrValue":63},{"baseDate":20170711,"baseTime":1500,"category":"RN1","nx":60,"ny":122,"obsrValue":0},{"baseDate":20170711,"baseTime":1500,"category":"SKY","nx":60,"ny":122,"obsrValue":3},{"baseDate":20170711,"baseTime":1500,"category":"T1H","nx":60,"ny":122,"obsrValue":30.2},{"baseDate":20170711,"baseTime":1500,"category":"UUU","nx":60,"ny":122,"obsrValue":1},{"baseDate":20170711,"baseTime":1500,"category":"VEC","nx":60,"ny":122,"obsrValue":243},{"baseDate":20170711,"baseTime":1500,"category":"VVV","nx":60,"ny":122,"obsrValue":0.5},{"baseDate":20170711,"baseTime":1500,"category":"WSD","nx":60,"ny":122,"obsrValue":1.1}]},"numOfRows":10,"pageNo":1,"totalCount":10}}}
     * @param x coord X
     * @param y coord Y
     * @return
     * @throws NetworkException
     * @throws JSONException
     * @throws Exception
     */
    public static ForecastCurrentObject requestCurrent(float x, float y) throws NetworkException, JSONException, Exception {
        Properties params = getParam();
        params.setProperty("base_date", getTodayDate());
        params.setProperty("base_time", getCurrentTime());
        params.setProperty("nx", String.valueOf((int)x));
        params.setProperty("ny", String.valueOf((int)y));

        String response = Network.get(protocol, hostPath, APIForecastGrib, null, params);
        JSONObject header = new JSONObject(response).getJSONObject("response").getJSONObject("header");
        if(header.getString("resultCode").equals("0000") && header.getString("resultMsg").equals("OK")) {
            JSONObject body = new JSONObject(response).getJSONObject("response").getJSONObject("body");
            JSONArray items = body.getJSONObject("items").getJSONArray("item");

            ForecastCurrentObject currentObject = new ForecastCurrentObject();
            for(int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                ForecastObject data = new Gson().fromJson(item.toString(), ForecastObject.class);
                currentObject.addItem(data);
            }
            return currentObject;
        }

        return null;
    }

    private static String getDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(date);
    }

    private static String getTime(Date time) {
        SimpleDateFormat sdfMinutes = new SimpleDateFormat("mm", Locale.getDefault());
        int minutes = Integer.valueOf(sdfMinutes.format(time));
        if(minutes < 40) {
            // API 제공 시간이 매시간 40분 마다 제공이 되므로 40분 이전이면 30분 전 데이터를 받아와야 함
            time = new Date(time.getTime() - (1000 * 60 * 30));
        }
        SimpleDateFormat sdf;
        if(minutes <= 30) {
            sdf = new SimpleDateFormat("HH00", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("HH30", Locale.getDefault());
        }
        return sdf.format(time);
    }

    public static String getCurrentTime() {
        return getTime(new Date());
    }

    public static String getTodayDate() {
        return getDate(new Date());
    }

    private static Properties getParam() {
        Properties param = new Properties();
        param.setProperty("ServiceKey", serviceKey);
        param.setProperty("_type", "json");
        return param;
    }
}
