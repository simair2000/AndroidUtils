package com.simair.android.androidutils.network.http;

/**
 * Created by simair on 16. 11. 17.
 */

public enum METHOD {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT"),
    ;

    public String name;

    METHOD(String name) {
        this.name = name;
    }
}
