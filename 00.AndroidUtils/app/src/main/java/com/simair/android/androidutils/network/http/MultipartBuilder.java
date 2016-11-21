package com.simair.android.androidutils.network.http;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * Created by simair on 16. 11. 21.
 */

public class MultipartBuilder {
    private static final String LF = "\r\n";
    private static final String LFLF = "\r\n\r\n";

    private String boundary;
    private String charset = "UTF-8";
    private int contentLength = 0;
    private ByteArrayOutputStream builder = new ByteArrayOutputStream();

    public MultipartBuilder(HttpURLConnection connection, String charset) throws IOException {
        if(!TextUtils.isEmpty(charset)) {
            this.charset = charset;
        }

        boundary = System.currentTimeMillis() + "";
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data;charset=" + this.charset + "; boundary=" + boundary);
    }

    public String addStringPart(String name, String value) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary).append(LF);
        sb.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LF);
        sb.append("Content-type: text/plain; charset=" + charset).append(LFLF);
        sb.append(value).append(LFLF);
        contentLength += sb.length();
        StringBuilder ret = new StringBuilder(sb);
        builder.write(sb.toString().getBytes());
        return ret.toString();
    }

    public String addJsonPart(String json, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary).append(LF);
        if(TextUtils.isEmpty(fileName)) {
            sb.append("Content-Disposition: form-data; name=\"meta-data\"").append(LF);
        } else {
            sb.append("Content-Disposition: form-data; name=\"meta-data\"; filename=\"" + fileName + "\"").append(LF);
        }
        sb.append("Content-type: application/json; charset=" + charset).append(LFLF);
        sb.append(json).append(LFLF);
        contentLength += sb.length();
        StringBuilder ret = new StringBuilder(sb);
        builder.write(sb.toString().getBytes());
        return ret.toString();
    }

    /**
     * mimeType을 명시적으로 넣지 않고 null로 주면 file확장자를 보고 적당히 결정한다.
     * @param name
     * @param file
     * @param mimeType
     */
    public String addFilePart(String name, File file, String mimeType) throws IOException {
        StringBuilder sb = new StringBuilder();
        String fileName = file.getName();
        if(TextUtils.isEmpty(mimeType)) {
            mimeType = URLConnection.guessContentTypeFromName(fileName);
        }
        sb.append("--" + boundary).append(LF);
        sb.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"").append(LF);
        sb.append("Content-Type: " + mimeType).append(LF);
        sb.append("Content-Transfer-Encoding: binary").append(LFLF);
        contentLength += sb.length();
        StringBuilder ret = new StringBuilder(sb);
        builder.write(sb.toString().getBytes());

        FileInputStream is = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int read = -1;
        while((read = is.read(buffer)) != -1) {
            contentLength += read;
            builder.write(buffer, 0, read);
        }
        is.close();
        builder.write(LFLF.getBytes()); contentLength++;
        return ret.toString();
    }

    public String addBinaryPart(String name, byte[] data, String fileName, String mimeType) throws IOException {
        StringBuilder sb = new StringBuilder();
        if(TextUtils.isEmpty(mimeType)) {
            mimeType = URLConnection.guessContentTypeFromName(fileName);
        }
        sb.append("--" + boundary).append(LF);
        sb.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"").append(LF);
        sb.append("Content-Type: " + mimeType).append(LF);
        sb.append("Content-Transfer-Encoding: binary").append(LFLF);
        contentLength += sb.length();
        StringBuilder ret = new StringBuilder(sb);
        builder.write(sb.toString().getBytes());

        builder.write(data); contentLength += data.length;
        builder.write(LFLF.getBytes()); contentLength++;
        return ret.toString();
    }

    public ByteArrayOutputStream build() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(LF);
        sb.append("--" + boundary + "--").append(LF);
        contentLength += sb.length();
        builder.write(sb.toString().getBytes());
        return builder;
    }
}
