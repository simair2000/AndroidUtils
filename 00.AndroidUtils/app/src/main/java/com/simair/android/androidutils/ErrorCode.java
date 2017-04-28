package com.simair.android.androidutils;

/**
 * Created by simair on 16. 11. 17.
 */

public enum ErrorCode {

    ERROR_OK(10000, "OK"),
    ERROR_URL(10001, "malformed url"),
    ERROR_CLIENT(10002, "client error"),
    ERROR_JSON(10003, "json exception"),
    ERROR_NETWORK_UNAVAILABLE(10004, "network unavailable"),
    ERROR_FACADE(10005, "네트워크 체인이 없습니다."),
    FILE_EXIST(10006, "파일이 이미 존재합니다."),

    ERROR_TCP_START_SERVER(11000, ""),
    ERROR_TCP_STOP_SERVER(11001, ""),

    ERROR_ETC(20000, "unknown exception"),
    HTTP_REQUEST_TIMEOUT(408, "network timeout"),
    ;

    public int code;
    public String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
