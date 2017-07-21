package com.simair.android.androidutils.openapi.land;

import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;
import com.simair.android.androidutils.openapi.land.data.LAWDCode;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by simair on 17. 7. 21.<br />
 * 국토교통부에서 제공하는 부동산거래 관련 정보 OPEN API<br />
 */

public class APILand {

    private static final String serviceKey = "rA5V0ir6KO4pTACUyKzsmTM4NuXzqTHgTCk4iVeKGHbq5zBbEsJWNQMau6rZVIgyJubgAlIjwbxppIsFWmUlIA==";

    private static volatile APILand instance;
    private String protocol;
    private String host;
    private String path;
    private String hostPath;
    private String hostPath8081;    // port 번호가 8081

    private APILand(){}

    public static APILand getInstance() {
        if(instance == null) {
            synchronized (APILand.class) {
                instance = new APILand();
                instance.initProperties();
            }
        }
        return instance;
    }

    private void initProperties() {
        protocol = "http";
        host = "openapi.molit.go.kr";
        path = "/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc";
        hostPath = host + path;
        hostPath8081 = host + ":8081" + path;
    }

    public String getLandTrade(LAWDCode code, Date date) throws NetworkException, JSONException, Exception {
        Properties params = getDefaultParam();
        params.setProperty("LAWD_CD", String.valueOf(code.code));
        params.setProperty("DEAL_YMD", new SimpleDateFormat("yyyyMM").format(date));
        String strResponse = Network.get(protocol, hostPath, "getRTMSDataSvcLandTrade", null, params);
        String strJson = new XmlToJson.Builder(strResponse).build().toString();
        return strJson;
    }

    private Properties getDefaultParam() {
        Properties params = new Properties();
        params.setProperty("serviceKey", serviceKey);
        return params;
    }
}
