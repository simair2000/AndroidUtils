package com.simair.android.androidutils.openapi.kakao.map;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.simair.android.androidutils.network.http.Network;
import com.simair.android.androidutils.openapi.kakao.CategoryGroup;
import com.simair.android.androidutils.openapi.kakao.CoordType;
import com.simair.android.androidutils.openapi.kakao.KaKaoAppKey;
import com.simair.android.androidutils.openapi.kakao.Sort;

import java.util.Properties;

/**
 * Daum Map 관련 API
 * 1. 주소검색
 * 2. 좌표 -> 행정구역정보
 * 3. 좌표 -> 주소
 * 4. 좌표계변환
 * 5. 키워드로 장소검색
 * 6. 카테고리로 장소검색
 */
public class APIDaumMap {
    private static final String appKey = KaKaoAppKey.KEY_REST_API.key;

    private static volatile APIDaumMap instance;
    private String protocol;
    private String host;
    private String path;
    private String hostPath;

    public static APIDaumMap getInstance() {
        if(instance == null) {
            synchronized (APIDaumMap.class) {
                instance = new APIDaumMap();
                instance.initProperties();
            }
        }
        return instance;
    }

    private void initProperties() {
        protocol = "https";
        host = "dapi.kakao.com";
        path = "/v2/local";
        hostPath = host + path;
    }

    /**
     * 주소검색
     * @param keyword
     * @return
     * @throws Exception
     */
    public String searchAddress(String keyword) throws Exception {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("query", keyword);

        String response = Network.get(protocol, hostPath, "search/address.json", header, body);
        return response;
    }

    /**
     * 좌표 -> 행정구역정보
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    public String requestRegionCode(double latitude, double longitude) throws Exception {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("x", String.valueOf(longitude));
        body.setProperty("y", String.valueOf(latitude));

        String response = Network.get(protocol, hostPath, "geo/coord2regioncode.json", header, body);
        return response;
    }

    /**
     * 좌표 -> 주소
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    public String requestAddress(double latitude, double longitude) throws Exception {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("x", String.valueOf(longitude));
        body.setProperty("y", String.valueOf(latitude));

        String response = Network.get(protocol, hostPath, "geo/coord2address.json", header, body);
        return response;
    }

    /**
     * 좌표계변환
     * @param inputType
     * @param outputType
     * @param x
     * @param y
     * @return
     * @throws Exception
     */
    public String convertCoord(CoordType inputType, CoordType outputType, String x, String y) throws Exception {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("input_coord", inputType.name());
        body.setProperty("output_coord", outputType.name());
        body.setProperty("x", String.valueOf(x));
        body.setProperty("y", String.valueOf(y));

        String response = Network.get(protocol, hostPath, "geo/transcoord.json", header, body);
        return response;
    }

    /**
     * 키워드로 장소검색
     * @param keyword
     * @param categoryGroup can be null
     * @param latitude
     * @param longitude
     * @param radius 0~20000 meter
     * @param sort
     * @return
     * @throws Exception
     */
    public String searchKeyword(String keyword, CategoryGroup categoryGroup, double latitude, double longitude, int radius, Sort sort) throws Exception {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("query", keyword);
        body.setProperty("x", String.valueOf(longitude));
        body.setProperty("y", String.valueOf(latitude));
        body.setProperty("radius", String.valueOf(radius));
        if(categoryGroup != CategoryGroup.ALL) {
            body.setProperty("category_group_code", categoryGroup.code);
        }
        if(sort != null) {
            body.setProperty("sort", sort.name());
        }

        String response = Network.get(protocol, hostPath, "search/keyword.json", header, body);
        return response;
    }

    public SearchCategoryResult searchCategory(CategoryGroup categoryGroup, double latitude, double longitude, int radius, Sort sort) throws Exception {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("category_group_code", categoryGroup.code);
        body.setProperty("x", String.valueOf(longitude));
        body.setProperty("y", String.valueOf(latitude));
        body.setProperty("radius", String.valueOf(radius));
        if(sort != null) {
            body.setProperty("sort", sort.name());
        }

        String response = Network.get(protocol, hostPath, "search/category.json", header, body);
        if(!TextUtils.isEmpty(response)) {
            return new Gson().fromJson(response, SearchCategoryResult.class);
        }
        return null;
    }
}
