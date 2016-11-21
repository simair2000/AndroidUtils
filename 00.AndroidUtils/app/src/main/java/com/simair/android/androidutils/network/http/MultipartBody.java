package com.simair.android.androidutils.network.http;

import java.io.File;

/**
 * Created by simair on 16. 11. 21.
 */

public class MultipartBody {
    public static final int TYPE_NONE = 0;
    public static final int TYPE_STRING = 1;
    public static final int TYPE_JSON = 2;
    public static final int TYPE_FILE = 3;
    public static final int TYPE_BINARY = 4;

    public int partType = 0;

    public String name;
    public String content;
    public String fileName;
    public String mimeType;
    public File file;
    public byte[] data;
}
