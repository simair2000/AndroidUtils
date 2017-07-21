package com.simair.android.androidutils.openapi.airpollution;

import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;

import org.json.JSONException;

import java.util.Properties;

/**
 * Created by simair on 17. 7. 20.
 * 한국 환경공단 국가 대기오염 정보 OPEN API<br />
 * 되도 않했네...졸라게 구림
 */
@Deprecated
public class APIAirPollution {

    private static final String serviceKey = "o4P1W26cv9cCzkHtvTZZsfEYc0/itc/se1bVFWgzrob0xiDZYx5w0awbexo800tXaaM74BA0FbIV174ENiFGWg==";

    private static volatile APIAirPollution instance;
    private String protocol;
    private String host;
    private String path;
    private String hostPath;

    private APIAirPollution(){}

    public static APIAirPollution getInstance() {
        if(instance == null) {
            synchronized (APIAirPollution.class) {
                instance = new APIAirPollution();
                instance.initProperties();
            }
        }
        return instance;
    }

    private void initProperties() {
        protocol = "http";
        host = "openapi.airkorea.or.kr";
        path = "/openapi/services/rest/MsrstnInfoInqireSvc";
        hostPath = host + path;
    }

    private Properties getParam() {
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
    public String getTMCoord(String keyword) throws NetworkException, JSONException, Exception {
        Properties params = getParam();
        params.setProperty("umdName", keyword);
        String strResponse = Network.get(protocol, hostPath, "getTMStdrCrdnt", null, params);

        return strResponse;
    }
}
