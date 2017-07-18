package com.simair.android.androidutils.openapi.visitkorea.data;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 18.
 */

public class ImageListParam implements Serializable {
    long contentId;
    long contentTypeId;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public long getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(long contentTypeId) {
        this.contentTypeId = contentTypeId;
    }
}
