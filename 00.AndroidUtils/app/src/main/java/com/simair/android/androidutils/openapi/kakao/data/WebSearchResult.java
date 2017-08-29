package com.simair.android.androidutils.openapi.kakao.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by simair on 17. 8. 28.
 */

public class WebSearchResult implements Serializable {
    @SerializedName("meta")             SearchMetaData metaData;
    @SerializedName("documents")        List<WebSearchDocument> result;

    public List<WebSearchDocument> getResult() {
        return result;
    }

    public void setResult(List<WebSearchDocument> result) {
        this.result = result;
    }

    public static WebSearchResult parse(String data) {
        if(!TextUtils.isEmpty(data)) {
            return new Gson().fromJson(data.toString(), WebSearchResult.class);
        }
        return null;
    }
}
