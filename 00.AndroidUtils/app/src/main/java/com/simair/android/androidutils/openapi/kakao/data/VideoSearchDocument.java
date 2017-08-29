package com.simair.android.androidutils.openapi.kakao.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 8. 29.
 */

public class VideoSearchDocument implements Serializable {
    @SerializedName("datetime")     String dateTime;
    @SerializedName("title")        String title;
    @SerializedName("play_time")    int playSeconds;
    @SerializedName("thumbnail")    String thumbnailUrl;
    @SerializedName("url")          String url;
    @SerializedName("author")       String author;

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

    public int getPlaySeconds() {
        return playSeconds;
    }

    public void setPlaySeconds(int playSeconds) {
        this.playSeconds = playSeconds;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
