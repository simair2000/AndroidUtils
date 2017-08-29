package com.simair.android.androidutils.openapi.kakao.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 8. 28.
 */

public class SearchMetaData implements Serializable {
    @SerializedName("total_count")      int totalCount;
    @SerializedName("pageable_count")   int pageableCount;
    @SerializedName("dup_count")        int dupCount;
    @SerializedName("is_end")           boolean isEnd;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageableCount() {
        return pageableCount;
    }

    public void setPageableCount(int pageableCount) {
        this.pageableCount = pageableCount;
    }

    public int getDupCount() {
        return dupCount;
    }

    public void setDupCount(int dupCount) {
        this.dupCount = dupCount;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
