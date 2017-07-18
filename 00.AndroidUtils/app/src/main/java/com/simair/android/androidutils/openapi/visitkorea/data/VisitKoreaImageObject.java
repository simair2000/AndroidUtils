package com.simair.android.androidutils.openapi.visitkorea.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 18.
 */

public class VisitKoreaImageObject implements Serializable {
    @SerializedName("contentid")        long contentId;
    @SerializedName("originimgurl")     String imgUrl;
    @SerializedName("smallimageurl")    String thumbnailUrl;
    @SerializedName("serialnum")        String serialNumber;

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
