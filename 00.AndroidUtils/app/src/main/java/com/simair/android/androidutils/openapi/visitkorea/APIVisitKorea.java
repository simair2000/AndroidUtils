package com.simair.android.androidutils.openapi.visitkorea;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaDetailCommonObject;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaImageObject;
import com.simair.android.androidutils.openapi.visitkorea.data.VisitKoreaLocationBasedListObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by simair on 17. 7. 18.
 * 한국 관광공사 OPEN API<br />
 * Sample Page : <a href = "http://api.visitkorea.or.kr/guide/inforArea.do">http://api.visitkorea.or.kr/guide/inforArea.do</a>
 */
public class APIVisitKorea {
    private static final String serviceKey = "o4P1W26cv9cCzkHtvTZZsfEYc0/itc/se1bVFWgzrob0xiDZYx5w0awbexo800tXaaM74BA0FbIV174ENiFGWg==";
    private static final String appName = " com.simair.android.androidutils";

    private static volatile APIVisitKorea instance;
    private String protocol;
    private String host;
    private String path;

    private APIVisitKorea(){}

    public static APIVisitKorea getInstance() {
        if(instance == null) {
            synchronized (APIVisitKorea.class) {
                instance = new APIVisitKorea();
                instance.initProperties();
            }
        }
        return instance;
    }

    private void initProperties() {
        protocol = "http";
        host = "api.visitkorea.or.kr";
        path = "openapi/service/rest/";
    }

    public ArrayList<VisitKoreaLocationBasedListObject> requestLocationBasedInfo(int count, int pageNo, TourType type, double longitude, double latitude, int radius) throws NetworkException, JSONException {
        Properties body = getDefaultParam();
        body.setProperty("numOfRows", String.valueOf(count));
        body.setProperty("pageNo", String.valueOf(pageNo));
        body.setProperty("listYN", "Y");
        body.setProperty("contentTypeId", String.valueOf(type.getCode()));
        body.setProperty("mapX", String.valueOf(longitude));
        body.setProperty("mapY", String.valueOf(latitude));
        body.setProperty("radius", String.valueOf(radius));

        String response = Network.get(protocol, host, path + getService() + "locationBasedList", null, body);
        if(!TextUtils.isEmpty(response)) {
            JSONObject json = new JSONObject(response);
            JSONObject header = json.getJSONObject("response").getJSONObject("header");
            if(header.getString("resultCode").equals("0000")) {
                JSONObject bodyObject = json.getJSONObject("response").getJSONObject("body");
                int total = bodyObject.getInt("totalCount");
                int page = bodyObject.getInt("pageNo");
                int numOfRows = bodyObject.getInt("numOfRows");

                JSONArray array = bodyObject.getJSONObject("items").getJSONArray("item");
                ArrayList<VisitKoreaLocationBasedListObject> ret = new ArrayList<>();
                for(int i = 0; i < array.length(); i++) {
                    String obj = array.getJSONObject(i).toString();
                    VisitKoreaLocationBasedListObject item = new Gson().fromJson(obj, VisitKoreaLocationBasedListObject.class);
                    item.setTotalCount(total);
                    item.setPageNo(page);
                    item.setNumOfRows(numOfRows);
                    ret.add(item);
                }

                return ret;
            }
        }
        return null;
    }

    public VisitKoreaDetailCommonObject requestDetailCommon(long contentId) throws NetworkException, JSONException {
        Properties body = getDefaultParam();
        body.setProperty("contentId", String.valueOf(contentId));
        body.setProperty("defaultYN", "Y");
        body.setProperty("firstImageYN", "Y");
        body.setProperty("addrinfoYN", "Y");
        body.setProperty("mapinfoYN", "Y");
        body.setProperty("overviewYN", "Y");
        body.setProperty("transGuideYN", "Y");

        String response = Network.get(protocol, host, path + getService() + "detailCommon", null, body);
        if(!TextUtils.isEmpty(response)) {
            JSONObject json = new JSONObject(response);
            JSONObject header = json.getJSONObject("response").getJSONObject("header");
            if(header.getString("resultCode").equals("0000")) {
                JSONObject bodyObject = json.getJSONObject("response").getJSONObject("body");
                JSONObject object = bodyObject.getJSONObject("items").getJSONObject("item");
                VisitKoreaDetailCommonObject item = new Gson().fromJson(object.toString(), VisitKoreaDetailCommonObject.class);
                return item;
            }
        }

        return null;
    }

    public ArrayList<VisitKoreaImageObject> requestImageList(long contentId, long contentTypeId) throws NetworkException, JSONException {
        Properties body = getDefaultParam();
        body.setProperty("contentId", String.valueOf(contentId));
        body.setProperty("contentTypeId", String.valueOf(contentTypeId));
        body.setProperty("imageYN", "Y");

        String response = Network.get(protocol, host, path + getService() + "detailImage", null, body);
        if(!TextUtils.isEmpty(response)) {
            JSONObject json = new JSONObject(response);
            JSONObject header = json.getJSONObject("response").getJSONObject("header");
            if(header.getString("resultCode").equals("0000")) {
                JSONObject bodyObject = json.getJSONObject("response").getJSONObject("body");
                JSONArray array = bodyObject.getJSONObject("items").getJSONArray("item");
                ArrayList<VisitKoreaImageObject> ret = new ArrayList<>();
                for(int i = 0; i < array.length(); i++) {
                    String obj = array.getJSONObject(i).toString();
                    VisitKoreaImageObject item = new Gson().fromJson(obj, VisitKoreaImageObject.class);
                    ret.add(item);
                }
                return ret;
            }
        }

        return null;
    }

    private Properties getDefaultParam() {
        Properties param = new Properties();
        param.setProperty("ServiceKey", serviceKey);
        param.setProperty("_type", "json");
        param.setProperty("MobileApp", appName);
        param.setProperty("MobileOS", "AND");
        return param;
    }

    private String getService() {
        String locale = Utils.getCurrentLocale();
        if(locale.contains("ko")) {
            return "KorService/";
        } else if(locale.contains("JP_ja")) {
            return "JpnService/";
        } else if(locale.contains("CN_zh")) {
            return "ChsService/";
        } else if(locale.contains("TW_zh")) {
            return "ChtService/";
        } else {
            return "EngService/";
        }
    }
}
