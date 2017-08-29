package com.simair.android.androidutils.openapi.kakao.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 8. 28.
 */

public class WebSearchDocument implements Serializable {
    @SerializedName("datetime")         String dateTime;
    @SerializedName("title")            String title;
    @SerializedName("contents")         String contents;
    @SerializedName("url")              String url;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
