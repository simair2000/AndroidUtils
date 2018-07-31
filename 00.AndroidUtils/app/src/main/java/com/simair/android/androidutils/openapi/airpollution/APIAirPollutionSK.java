package com.simair.android.androidutils.openapi.airpollution;

import com.google.gson.Gson;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;
import com.simair.android.androidutils.openapi.airpollution.data.DustObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by simair on 17. 7. 21.<br />
 * 한국 환경 공단 OPEN API 가 구려 터져서 SK planet Developer Open API를 사용하기로 하였음..<br />
 * site : <a href="https://developers.skplanetx.com/apidoc/kor/weather">https://developers.skplanetx.com/apidoc/kor/weather/</a>
 */

@Deprecated
public class APIAirPollutionSK {
    private static final String appKey = "8437c528-74cf-3513-a5fd-b34923aec856";
    private static final String userId = "simair2000";

    private static volatile APIAirPollutionSK instance;
    private String protocol;
    private String host;
    private String path;
    private String hostPath;

    private APIAirPollutionSK(){}

    public static APIAirPollutionSK getInstance() {
        if(instance == null) {
            synchronized (APIAirPollutionSK.class) {
                instance = new APIAirPollutionSK();
                instance.initProperties();
            }
        }
        return instance;
    }

    private void initProperties() {
        protocol = "http";
        host = "apis.skplanetx.com";
        path = "/weather";
        hostPath = host + path;
    }

    private Properties getBodyParam() {
        Properties param = new Properties();
        param.setProperty("version", "1");
        return param;
    }

    private Properties getHeaderParam() {
        Properties param = new Properties();
        param.setProperty("x-skpop-userId", userId);
        param.setProperty("Accept-Language", "ko_KR");
        param.setProperty("Accept", "application/json");
        param.setProperty("appKey", appKey);
        return param;
    }

    /**
     * 미세먼지 정보
     * @param latitude
     * @param longitude
     * @return
     * @throws NetworkException
     * @throws JSONException
     * @throws Exception
     */
    public ArrayList<DustObject> getDust(double latitude, double longitude) throws NetworkException, JSONException, Exception {
        Properties body = getBodyParam();
        body.setProperty("lat", String.valueOf(latitude));
        body.setProperty("lon", String.valueOf(longitude));
        String strResponse = Network.get(protocol, hostPath, "dust", getHeaderParam(), body);
        JSONObject result = new JSONObject(strResponse).getJSONObject("result");
        if(result.getInt("code") == 9200) {
            // success
            JSONArray dust = new JSONObject(strResponse).getJSONObject("weather").getJSONArray("dust");
            if(dust != null && dust.length() > 0) {
                ArrayList<DustObject> ret = new ArrayList<>();
                for(int i = 0; i < dust.length(); i++) {
                    JSONObject json = dust.getJSONObject(i);
                    DustObject object = new Gson().fromJson(json.toString(), DustObject.class);
                    ret.add(object);
                }
                return ret;
            }
        }
        return null;
    }
}
