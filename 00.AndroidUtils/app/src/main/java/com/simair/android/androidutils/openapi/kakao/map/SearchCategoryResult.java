package com.simair.android.androidutils.openapi.kakao.map;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchCategoryResult implements Serializable {
    @SerializedName("meta")         public  Meta meta;
    @SerializedName("documents")    public  List<Document> documents;

    public class Meta implements Serializable {
        @SerializedName("total_count")      public int totalCount;
        @SerializedName("pageable_count")   public int pageableCount;
        @SerializedName("is_end")           public boolean isEnd;
        @SerializedName("same_name")        public String sameName;
    }

    public class Document implements Serializable {
        @SerializedName("id")                   public String id;
        @SerializedName("place_name")           public String placeName;
        @SerializedName("category_name")        public String categoryName;
        @SerializedName("category_group_code")  public String categoryGroupCode;
        @SerializedName("category_group_name")  public String categoryGroupName;
        @SerializedName("phone")                public String phone;
        @SerializedName("address_name")         public String addressName;
        @SerializedName("road_address_name")    public String roadAddressName;
        @SerializedName("x")                    public String x;
        @SerializedName("y")                    public String y;
        @SerializedName("place_url")            public String placeUrl;
        @SerializedName("distance")             public int distance;   // meter
    }
}
