package com.simair.android.androidutils;

import android.content.Context;

/**
 * Created by simair on 16. 11. 17.
 */

public class CustomException extends Exception {
    public int messageResId;
    public int code;
    public String message;

    public CustomException(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public CustomException(int code, int messageResId) {
        super();
        this.code = code;
        this.messageResId = messageResId;
    }

    public CustomException(Context context, int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
}
