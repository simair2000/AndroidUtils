package com.simair.android.androidutils.openapi.kakao.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 8. 30.
 */

public class ImageSearchDocument implements Serializable {
    @SerializedName("collection")           String collection;  // 분류
    @SerializedName("datetime")             String dateTime;
    @SerializedName("width")                int width;
    @SerializedName("height")               int height;
    @SerializedName("thumbnail_url")        String thumbnailUrl;
    @SerializedName("image_url")            String imageUrl;
    @SerializedName("display_sitename")     String source;  // 출처
    @SerializedName("doc_url")              String sourceUrl;   // 출처URL

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
