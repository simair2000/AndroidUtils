package com.simair.android.androidutils.openapi.kakao.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by simair on 17. 8. 29.
 */

public class VideoSearchResult implements Serializable {
    @SerializedName("meta")         SearchMetaData metaData;
    @SerializedName("documents")    List<VideoSearchDocument> result;

    public SearchMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(SearchMetaData metaData) {
        this.metaData = metaData;
    }

    public List<VideoSearchDocument> getResult() {
        return result;
    }

    public void setResult(List<VideoSearchDocument> result) {
        this.result = result;
    }

    public static VideoSearchResult parse(String data) {
        if(!TextUtils.isEmpty(data)) {
            return new Gson().fromJson(data.toString(), VideoSearchResult.class);
        }
        return null;
    }
}
