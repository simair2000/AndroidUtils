package com.simair.android.androidutils.network;

import android.content.Context;

import com.simair.android.androidutils.ErrorCode;

/**
 * Created by simair on 16. 11. 17.
 */

public class NetworkException extends Exception {
    public int messageResId;
    public int code;
    public String message;

    public NetworkException(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public NetworkException(ErrorCode error) {
        super();
        this.code = error.code;
        this.message = error.message;
    }

    public NetworkException(int code, int messageResId) {
        super();
        this.code = code;
        this.messageResId = messageResId;
    }

    public NetworkException(Context context, int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
}
