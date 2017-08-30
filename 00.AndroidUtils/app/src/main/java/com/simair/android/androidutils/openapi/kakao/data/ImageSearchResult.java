package com.simair.android.androidutils.openapi.kakao.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by simair on 17. 8. 30.
 */

public class ImageSearchResult implements Serializable {
    @SerializedName("meta")         SearchMetaData metaData;
    @SerializedName("documents")    List<ImageSearchDocument> result;

    public SearchMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(SearchMetaData metaData) {
        this.metaData = metaData;
    }

    public List<ImageSearchDocument> getResult() {
        return result;
    }

    public void setResult(List<ImageSearchDocument> result) {
        this.result = result;
    }

    public static ImageSearchResult parse(String data) {
        if(!TextUtils.isEmpty(data)) {
            return new Gson().fromJson(data.toString(), ImageSearchResult.class);
        }
        return null;
    }
}
