package com.simair.android.androidutils.openapi.kakao;

import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;
import com.simair.android.androidutils.openapi.kakao.data.ImageSearchResult;
import com.simair.android.androidutils.openapi.kakao.data.VideoSearchResult;
import com.simair.android.androidutils.openapi.kakao.data.WebSearchResult;

import org.json.JSONException;

import java.util.Properties;

/**
 * Created by simair on 17. 8. 28.<br />
 * 카카오 검색관련 OPEN API<br />
 * 1. 웹문서 검색<br />
 * 2. 동영상 검색<br />
 * 3. 이미지 검색<br />
 * 4. 블로그 검색<br />
 * 5. 팁 검색<br />
 * 6. 책 검색<br />
 * 7. 카페 검색
 */

public class APISearch {
    private static final String appKey = "e6844a2f1a38385b9739daa75f36e34a";

    private static volatile APISearch instance;
    private String protocol;
    private String host;
    private String path;
    private String hostPath;

    public enum SortParam {
        accuracy,   // 정확도순
        recency,    // 최신순
        ;
    }

    private APISearch(){}

    public static APISearch getInstance() {
        if(instance == null) {
            synchronized (APISearch.class) {
                instance = new APISearch();
                instance.initProperties();
            }
        }
        return instance;
    }

    private void initProperties() {
        protocol = "https";
        host = "dapi.kakao.com";
        path = "/v2/search";
        hostPath = host + path;
    }

    /**
     * 웹문서 검색
     * @param keyword 검색 키워드
     * @param sort 정렬
     * @param page 결과 페이지 번호 default [1]
     * @param count 한 페이지의 크기 default [10]
     * @return
     * @throws NetworkException
     * @throws JSONException
     */
    public WebSearchResult requestWebSearch(String keyword, SortParam sort, int page, int count) throws NetworkException, JSONException {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("query", keyword);
        body.setProperty("sort", sort.name());
        body.setProperty("page", (page < 1) ? "1" : String.valueOf(page));
        body.setProperty("size", (count < 10) ? "10" : String.valueOf(count));

        String response = Network.get(protocol, hostPath, "web", header, body);
        return WebSearchResult.parse(response);
    }

    /**
     * 동영상 검색
     * @param keyword
     * @param sort
     * @param page
     * @param count
     * @return
     * @throws NetworkException
     * @throws JSONException
     */
    public VideoSearchResult requestVideoSearch(String keyword, SortParam sort, int page, int count) throws NetworkException, JSONException {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("query", keyword);
        body.setProperty("sort", sort.name());
        body.setProperty("page", (page < 1) ? "1" : String.valueOf(page));
        body.setProperty("size", (count < 10) ? "10" : String.valueOf(count));

        String response = Network.get(protocol, hostPath, "vclip", header, body);
        return VideoSearchResult.parse(response);
    }

    /**
     * 이미지 검색
     * @param keyword
     * @param sort
     * @param page
     * @param count
     * @return
     * @throws NetworkException
     * @throws JSONException
     */
    public ImageSearchResult requestImageSearch(String keyword, SortParam sort, int page, int count) throws NetworkException, JSONException {
        Properties header = new Properties();
        header.setProperty("Authorization", "KakaoAK " + appKey);

        Properties body = new Properties();
        body.setProperty("query", keyword);
        body.setProperty("sort", sort.name());
        body.setProperty("page", (page < 1) ? "1" : String.valueOf(page));
        body.setProperty("size", (count < 10) ? "10" : String.valueOf(count));

        String response = Network.get(protocol, hostPath, "image", header, body);
        return ImageSearchResult.parse(response);
    }
}
