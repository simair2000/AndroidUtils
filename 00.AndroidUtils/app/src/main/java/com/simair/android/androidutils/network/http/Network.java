package com.simair.android.androidutils.network.http;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.ErrorCode;
import com.simair.android.androidutils.network.NetworkException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by simair on 16. 11. 17.
 */

public class Network {
    private static final String TAG = Network.class.getSimpleName();

    private static final int RETRY_COUNT = 2; // request 실패 시 2번까지 retry 해본다
    public static final int TIME_OUT = 5000;
    private static final boolean requestGZIP = true;
    private static final String POST_JSON = "SINGLE PART POST - JSON BODY";
    private static int tryCount = 1;
    private static boolean SecureInited = false;

    public static void useRetryCount(boolean useRetry) {
        if(useRetry) {
            tryCount = RETRY_COUNT;
        } else {
            tryCount = 1;
        }
    }


    public static String get(String protocol, String host, String api, Properties header, Properties body, String authUser, String authPasswd) throws NetworkException {
        Log.v(TAG, "get with Authorization");
        return request(protocol + "://" + host + "/" + api, METHOD.GET, header, body, TIME_OUT, authUser, authPasswd, null);
    }

    public static String get(String protocol, String host, String api, Properties header, Properties body) throws NetworkException {
        Log.v(TAG, "get");
        return request(protocol + "://" + host + "/" + api, METHOD.GET, header, body, TIME_OUT, null, null, null);
    }

    public static String post(String protocol, String host, String api, Properties header) throws NetworkException {
        Log.v(TAG, "post");
        return request(protocol + "://" + host + "/" + api, METHOD.POST, header, null, TIME_OUT, null, null, null);
    }

    public static String post(String protocol, String host, String api, Properties header, Properties body) throws NetworkException {
        Log.v(TAG, "post");
        return request(protocol + "://" + host + "/" + api, METHOD.POST, header, body, TIME_OUT, null, null, null);
    }

    public static String post(String protocol, String host, String api, Properties header, Properties queryParam, String jsonBody) throws NetworkException {
        Log.v(TAG, "post - json body");
        Properties body = new Properties();
        body.setProperty(POST_JSON, jsonBody);
        return request(protocol + "://" + host + "/" + api, METHOD.POST, header, body, TIME_OUT, null, null, queryParam);
    }

    public static String post(String protocol, String host, String api, Properties header, String jsonBody) throws NetworkException {
        Log.v(TAG, "post - json body");
        Properties body = new Properties();
        body.setProperty(POST_JSON, jsonBody);
        return request(protocol + "://" + host + "/" + api, METHOD.POST, header, body, TIME_OUT, null, null, null);
    }

    public static String put(String protocol, String host, String api, Properties header, Properties body) throws NetworkException {
        Log.v(TAG, "put");
        if(header == null) {
            header = new Properties();
        }
        header.put("X-HTTP-Method-Override", "PUT");
        return request(protocol + "://" + host + "/" + api, METHOD.POST, header, body, TIME_OUT, null, null, null);
    }

    public static String delete(String protocol, String host, String api, Properties header, Properties body) throws NetworkException {
        Log.v(TAG, "delete");
        return request(protocol + "://" + host + "/" + api, METHOD.DELETE, header, body, TIME_OUT, null, null, null);
    }

    public static String upload(String protocol, String host, String api, Properties header, String mimeType, byte[] raw) throws NetworkException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        String endpoint = protocol + "://" + host + "/" + api;
        HttpURLConnection conn = null;
        try {
            URL url = null;
            try {
                url = new URL(endpoint);
            } catch (MalformedURLException e) {
                tryCount = 1;
                throw new NetworkException(ErrorCode.ERROR_URL.code, e.getLocalizedMessage());
            }
            conn = getHttpURLConnection(url);
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(0);
            conn.setDoInput(true);
            conn.setRequestMethod(METHOD.POST.name);
            // cache-control
            conn.setUseCaches(false);
            conn.setRequestProperty("cache-control", "no-cache");
            // connection-control
            conn.setRequestProperty("connection", "keep-alive");

            if(header != null) {
                Iterator<Object> itr = header.keySet().iterator();
//                Log.v(TAG, "request - header ; start");
                while (itr.hasNext()) {
                    String field = (String) itr.next();
                    String value = header.getProperty(field);
                    conn.setRequestProperty(field, value);
//                    Log.v(TAG, "request - header - key : " + field + ", value : " + value);
                }
//                Log.v(TAG, "request - header ; end");
            }

            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", mimeType);
            conn.setFixedLengthStreamingMode(raw.length);

            // LOG Start
            Log.d(TAG + ".RAW", "POST " + endpoint + " " + "HTTP/1.1    - tryCount : " + tryCount);
            Log.d(TAG + ".RAW", "Host: " + url.getHost() + ":" + url.getPort());
            Map<String, List<String>> headers = conn.getRequestProperties();
            Set<String> keys = headers.keySet();
            for(String name : keys) {
                String property = name + ": ";
                List<String> values = headers.get(name);
                for(int i = 0; i < values.size(); i++) {
                    String value = values.get(i);
                    if(i == 0) {
                        property += value;
                    } else {
                        property += ";" + value;
                    }
                }
                Log.d(TAG + ".RAW", property);
            }
            Log.d(TAG + ".RAW", "\r\n");
            // LOG end

            OutputStream out = conn.getOutputStream();
            out.write(raw);
            out.close();

            int status = conn.getResponseCode();
            Log.d(TAG + ".RAW", "status : " + status);
            // check HTTP Errors
            if(status != 200) {
                if(RETRY_COUNT <= tryCount) {
                    tryCount = 1;
                    Log.e(TAG + ".RAW", "request - status : " + status + ", conn.getResponseMessage() - " + conn.getResponseMessage());
                    throw new NetworkException(status, conn.getResponseMessage());
                } else {
                    tryCount++;
                    return upload(protocol, host, api, header, mimeType, raw);
                }
            }

            // check Encoding
            InputStream is = conn.getInputStream();
            String encoding = conn.getHeaderField("Content-Encoding");
            if(encoding != null && encoding.trim().compareToIgnoreCase("gzip") == 0) {
                is = new GZIPInputStream(is);
            }

            String response = InputStreamToString(is);
            Log.v(TAG + ".RAW", "request - response : " + response);
            tryCount = 1;

            return response;
        } catch (SocketTimeoutException e) {
            // time out은 공통 처리한다
            tryCount = 1;
            Log.e(TAG + ".RAW", "request timeout : " + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "request time out" : e.getLocalizedMessage()));
            throw new NetworkException(ErrorCode.HTTP_REQUEST_TIMEOUT.code, ErrorCode.HTTP_REQUEST_TIMEOUT.message);
        } catch (IOException e) {
            if(RETRY_COUNT <= tryCount) {
                tryCount = 1;
                Log.e(TAG + ".RAW", "request ; " + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "unknown exception" : e.getLocalizedMessage()));
                throw new NetworkException(ErrorCode.ERROR_CLIENT.code, (TextUtils.isEmpty(e.getLocalizedMessage()) ? "unknown exception" : e.getLocalizedMessage()));
            } else {
                tryCount++;
                return upload(protocol, host, api, header, mimeType, raw);
            }
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String upload(String protocol, String host, String api, Properties header, List<MultipartBody> multipartList) throws NetworkException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        String endpoint = protocol + "://" + host + "/" + api;
        String originEndpoint = new String(endpoint);
        HttpURLConnection conn = null;
        try {
            URL url = null;
            try {
                url = new URL(endpoint);
            } catch (MalformedURLException e) {
                tryCount = 1;
                throw new NetworkException(ErrorCode.ERROR_URL.code, e.getLocalizedMessage());
            }
            conn = getHttpURLConnection(url);
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(0);
            conn.setDoInput(true);
            conn.setRequestMethod(METHOD.POST.name);
            // cache-control
            conn.setUseCaches(false);
            conn.setRequestProperty("cache-control", "no-cache");
            // connection-control
            conn.setRequestProperty("connection", "close");

            if(header != null) {
                if(requestGZIP) {
                    header.setProperty("Accept-Encoding", "gzip");
                }
                Iterator<Object> itr = header.keySet().iterator();
//                Log.v(TAG, "request - header ; start");
                while (itr.hasNext()) {
                    String field = (String) itr.next();
                    String value = header.getProperty(field);
                    conn.setRequestProperty(field, value);
//                    Log.v(TAG, "request - header - key : " + field + ", value : " + value);
                }
//                Log.v(TAG, "request - header ; end");
            }

            boolean includeBinaryPart = false;
            MultipartBuilder builder = new MultipartBuilder(conn, null);
            StringBuilder sb =new StringBuilder();
            if(multipartList != null && multipartList.size() > 0) {
                for(MultipartBody part : multipartList) {
                    if(part.partType == MultipartBody.TYPE_JSON) {
                        sb.append(builder.addJsonPart(part.content, part.fileName));
                    } else if(part.partType == MultipartBody.TYPE_FILE) {
                        includeBinaryPart = true;
                        sb.append(builder.addFilePart(part.name, part.file, part.mimeType));
                    } else if(part.partType == MultipartBody.TYPE_STRING) {
                        sb.append(builder.addStringPart(part.name, part.content));
                    } else if(part.partType == MultipartBody.TYPE_BINARY) {
                        includeBinaryPart = true;
                        sb.append(builder.addBinaryPart(part.name, part.data, part.fileName, part.mimeType));
                    }
                }
            }
            ByteArrayOutputStream bOut = builder.build();
//            Log.v(TAG, stringBody);
            conn.setFixedLengthStreamingMode(bOut.size());

            // LOG Start
            Log.d(TAG + ".RAW", "POST " + endpoint + " " + "HTTP/1.1    - tryCount : " + tryCount);
            Log.d(TAG + ".RAW", "Host: " + url.getHost() + ":" + url.getPort());
            Map<String, List<String>> headers = conn.getRequestProperties();
            Set<String> keys = headers.keySet();
            for(String name : keys) {
                String property = name + ": ";
                List<String> values = headers.get(name);
                for(int i = 0; i < values.size(); i++) {
                    String value = values.get(i);
                    if(i == 0) {
                        property += value;
                    } else {
                        property += ";" + value;
                    }
                }
                Log.d(TAG + ".RAW", property);
            }
            Log.d(TAG + ".RAW", "\r\n");
            if(!includeBinaryPart) {
                String[] lines = new String(bOut.toByteArray()).split(System.getProperty("line.separator"));
                for(String line : lines) {
                    Log.d(TAG + ".RAW", line);
                }
            } else {
                Log.d(TAG + ".RAW", sb.toString());
            }
            // LOG end

            OutputStream out = conn.getOutputStream();
            bOut.writeTo(out);
            out.close();
            bOut.close();

            int status = conn.getResponseCode();
            Log.d(TAG + ".RAW", "status : " + status);
            // check HTTP Errors
            if(status != 200) {
                if(RETRY_COUNT <= tryCount) {
                    tryCount = 1;
                    Log.e(TAG + ".RAW", "request - status : " + status + ", conn.getResponseMessage() - " + conn.getResponseMessage());
                    throw new NetworkException(status, conn.getResponseMessage());
                } else {
                    tryCount++;
                    return upload(protocol, host, api, header, multipartList);
                }
            }

            // check Encoding
            InputStream is = conn.getInputStream();
            String encoding = conn.getHeaderField("Content-Encoding");
            if(encoding != null && encoding.trim().compareToIgnoreCase("gzip") == 0) {
                is = new GZIPInputStream(is);
            }

            String response = InputStreamToString(is);
            Log.v(TAG + ".RAW", "request - response : " + response);
            tryCount = 1;

            return response;
        } catch (SocketTimeoutException e) {
            // time out은 공통 처리한다
            tryCount = 1;
            Log.e(TAG + ".RAW", "request timeout : " + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "request time out" : e.getLocalizedMessage()));
            throw new NetworkException(ErrorCode.HTTP_REQUEST_TIMEOUT.code, ErrorCode.HTTP_REQUEST_TIMEOUT.message);
        } catch (IOException e) {
            if(RETRY_COUNT <= tryCount) {
                tryCount = 1;
                Log.e(TAG + ".RAW", "request ; " + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "unknown exception" : e.getLocalizedMessage()));
                throw new NetworkException(ErrorCode.ERROR_CLIENT.code, (TextUtils.isEmpty(e.getLocalizedMessage()) ? "unknown ioexception" : e.getLocalizedMessage()));
            } else {
                tryCount++;
                return upload(protocol, host, api, header, multipartList);
            }
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * CPS 컨텐츠를 다운로드 한다</br>
     * 저장될 컨텐츠의 파일 명은 {pid.zip} 가 된다. 예를 들어 pid가 123 이면 123.zip 이런식으로 저장한다.
     * @param strUrl 다운로드 URL ( query parameter 제외 )
     * @param outDir 컨텐츠 파일이 저장될 디렉토리
     * @param header http header
     * @param body pid 가 필수로 들어가야 함
     * @param handler 다운로드 진행률을 받고 싶으면 설정한다. 필요 없으면 null 을 넣으면 됨.
     * @return
     * @throws NetworkException
     * @throws JSONException
     */
    public static File cpsDownload(String strUrl, File outDir, Properties header, Properties body, Command.CommandHandler handler) throws NetworkException, JSONException {
        URL url = null;

        String bodyString = "";
        try {
            url = new URL(strUrl);
            bodyString = makeBodyString(body);
            if(!TextUtils.isEmpty(bodyString)) {
                strUrl += "?";
                strUrl += bodyString;
                url = new URL(strUrl);
            }
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        }

        InputStream is = null;
        OutputStream os = null;
        BufferedOutputStream bos = null;
        HttpURLConnection conn;
        File file = null;
        try {
            conn = getHttpURLConnection(url);
            if(header != null) {
                Iterator<Object> itr = header.keySet().iterator();
                while (itr.hasNext()) {
                    String field = (String) itr.next();
                    String value = header.getProperty(field);
                    conn.setRequestProperty(field, value);
                }
            }
            String pid = body.getProperty("pid");
            String name = pid + ".zip";
            file = new File(outDir, name);
            is = conn.getInputStream();
            os = new FileOutputStream(file);
            bos = new BufferedOutputStream(os);

            int fileLength = conn.getContentLength();
            Log.d("Network.RAW", "zip file length : " + fileLength);
            if(handler != null) {
                handler.sendMessage(handler.obtainMessage(Command.WHAT_DOWN_START, fileLength, 0, strUrl));
            }

            int bufferLength = 0;
            byte[] buffer = new byte[1024];
            while((bufferLength = is.read(buffer)) > 0) {
                bos.write(buffer, 0, bufferLength);
                if(handler != null) {
                    handler.sendMessage(handler.obtainMessage(Command.WHAT_DOWNLOADING, bufferLength, 0, strUrl));
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            bos.flush();
            Log.d("Network.RAW", "zip file length : " + conn.getHeaderField("Contents-Length"));
        } catch (SocketTimeoutException e) {
            // time out은 공통 처리한다
            tryCount = 1;
            Log.e(TAG + ".RAW", "request timeout : " + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "request time out" : e.getLocalizedMessage()));
            throw new NetworkException(ErrorCode.HTTP_REQUEST_TIMEOUT.code, ErrorCode.HTTP_REQUEST_TIMEOUT.message);
        } catch (IOException e) {
            Log.e(TAG, "download ; " + e.getLocalizedMessage());
            e.printStackTrace();
            throw new NetworkException(ErrorCode.ERROR_CLIENT.code, e.getLocalizedMessage());
        } finally {
            try {
                if(handler != null) {
                    handler.sendMessage(handler.obtainMessage(Command.WHAT_DOWN_END, strUrl));
                }
                if(os != null) {
                    os.close();
                }
                if(is != null) {
                    is.close();
                }
                if(bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new NetworkException(ErrorCode.ERROR_CLIENT.code, e.getLocalizedMessage());
            }
        }
        return file;
    }

    public static String download(String strUrl, String folderPath, String fileName, Command.CommandHandler handler) throws NetworkException {
        URL url;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            throw new NetworkException(ErrorCode.ERROR_URL.code, strUrl);
        }

        InputStream is = null;
        OutputStream os = null;
        BufferedOutputStream bos = null;
        HttpURLConnection conn;
        File file = null;
        try {
            conn = getHttpURLConnection(url);
            // set Header info
//            conn.setRequestProperty(HttpHeader.TN_IUID.name, HttpHeader.TN_IUID.getValue(Process.instance));
//            conn.setRequestProperty(HttpHeader.TN_AUTH_KEY.name, HttpHeader.TN_AUTH_KEY.getValue(Process.instance));

            if(TextUtils.isEmpty(fileName)) {
                String[] tokens = strUrl.split("/");
                String name = tokens[tokens.length - 1];    // file name to download
                file = new File(folderPath + "/" + name);
            } else {
                file = new File(folderPath + "/" + fileName);
            }
            is = conn.getInputStream();
            os = new FileOutputStream(file);
            bos = new BufferedOutputStream(os);

            int fileLength = conn.getContentLength();
            if(handler != null) {
                handler.sendMessage(handler.obtainMessage(Command.WHAT_DOWN_START, fileLength, 0, strUrl));
            }

            int bufferLength = 0;
            byte[] buffer = new byte[1024];
            while((bufferLength = is.read(buffer)) > 0) {
                bos.write(buffer, 0, bufferLength);
                if(handler != null) {
                    handler.sendMessage(handler.obtainMessage(Command.WHAT_DOWNLOADING, bufferLength, 0, strUrl));
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            bos.flush();
        } catch (SocketTimeoutException e) {
            // time out은 공통 처리한다
            tryCount = 1;
            Log.e(TAG + ".RAW", "request timeout : " + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "request time out" : e.getLocalizedMessage()));
            throw new NetworkException(ErrorCode.HTTP_REQUEST_TIMEOUT.code, ErrorCode.HTTP_REQUEST_TIMEOUT.message);
        } catch (IOException e) {
            Log.e(TAG, "download ; " + e.getLocalizedMessage());
            throw new NetworkException(ErrorCode.ERROR_CLIENT.code, e.getLocalizedMessage());
        } finally {
            try {
                if(handler != null) {
                    handler.sendMessage(handler.obtainMessage(Command.WHAT_DOWN_END, strUrl));
                }
                if(os != null) {
                    os.close();
                }
                if(is != null) {
                    is.close();
                }
                if(bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new NetworkException(ErrorCode.ERROR_CLIENT.code, e.getLocalizedMessage());
            }
        }
        return file.getAbsolutePath();
    }

    private static String ReaderToString(Reader reader) {
        StringBuilder sb = new StringBuilder();
        try {
            String line = null;
            BufferedReader in = new BufferedReader(reader);
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
//            Log.w(TAG, e.getLocalizedMessage());
        }

        return sb.toString();
    }

    private static String request(String endpoint, METHOD method, Properties header, Properties body, int timeout, String authUser, String authPasswd, Properties queryParam) throws NetworkException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        String originEndpoint = new String(endpoint);
        HttpURLConnection conn = null;
        try {
            String jsonBody = null;
            if(body != null) {
                jsonBody = body.getProperty(POST_JSON, "");
            }
            String bodyString = "";
            if(TextUtils.isEmpty(jsonBody)) {
                bodyString = makeBodyString(body);
                if(!TextUtils.isEmpty(bodyString) && (method.equals(METHOD.GET) || method.equals(METHOD.DELETE))) {
                    if(endpoint.contains("?")) {
                        endpoint += "%";
                    } else {
                        endpoint += "?";
                    }
                    endpoint += bodyString;
                }
            }

            if(queryParam != null) {
                String queryString = makeBodyString(queryParam);
                if(!TextUtils.isEmpty(queryString) && method.equals(METHOD.POST)) {
                    endpoint += "?" + queryString;
                }
            }

            URL url;
            try {
                url = new URL(endpoint);
            } catch (MalformedURLException e) {
                tryCount = 1;
                throw new NetworkException(ErrorCode.ERROR_URL.code, e.getLocalizedMessage());
            }
            conn = getHttpURLConnection(url);
            conn.setRequestMethod(method.name);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(0);
            // cache-control
            conn.setUseCaches(false);
            conn.setRequestProperty("cache-control", "no-cache");
            // connection-control
            conn.setRequestProperty("connection", "close");
            conn.setDoInput(true);
            if(method.equals(METHOD.POST)) {
                conn.setDoOutput(true);
            }
            if(TextUtils.isEmpty(jsonBody)) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            } else {
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Content-length", jsonBody.getBytes("UTF-8").length + "");
            }

            // auth header
            if(!TextUtils.isEmpty(authUser) && !TextUtils.isEmpty(authPasswd)) {
                if(header == null) {
                    header = new Properties();
                }
                String B64userAndPasswd = Base64.encodeToString((authUser + ":" + authPasswd).getBytes(), 0);
                header.setProperty("Authorization", "Basic " + B64userAndPasswd);
                conn.setRequestProperty("Content-Type", "application/json");
            }

            if(header != null) {
                if(requestGZIP) {
                    header.setProperty("Accept-Encoding", "gzip");
                }
                Iterator<Object> itr = header.keySet().iterator();
                while (itr.hasNext()) {
                    String field = (String) itr.next();
                    String value = header.getProperty(field);
                    conn.setRequestProperty(field, value);
                }
            }

            byte[] postBytes = null;
            if(method.equals(METHOD.POST)) {
                if(TextUtils.isEmpty(jsonBody)) {
                    postBytes = bodyString.getBytes();
                } else {
                    postBytes = jsonBody.getBytes("UTF-8");
                }
                conn.setFixedLengthStreamingMode(postBytes.length);
            }

            Log.d(TAG + ".RAW", method.name + " " + endpoint + " " + "HTTP/1.1      - tryCount : " + tryCount);
            Log.d(TAG + ".RAW", "Host: " + url.getHost() + ":" + url.getPort());
            Map<String, List<String>> headers = conn.getRequestProperties();
            Set<String> keys = headers.keySet();
            for(String name : keys) {
                String property = name + ": ";
                List<String> values = headers.get(name);
                for(int i = 0; i < values.size(); i++) {
                    String value = values.get(i);
                    if(i == 0) {
                        property += value;
                    } else {
                        property += ";" + value;
                    }
                }
                Log.d(TAG + ".RAW", property);
            }
            if(method.equals(METHOD.POST)) {
                Log.d(TAG + ".RAW", "\r\n");
                Log.d(TAG + ".RAW", new String(postBytes));
            }

            if(method.equals(METHOD.POST)) {
                OutputStream out = conn.getOutputStream();
                out.write(postBytes);
                out.close();
            }

            int status = conn.getResponseCode();
            Log.d(TAG + ".RAW", "status : " + status);
            // check HTTP Errors
            if(method.equals(METHOD.POST) || method.equals(METHOD.GET) || method.equals(METHOD.DELETE)) {
                if(status != 200) {
                    if(status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_SEE_OTHER) {
                        // redirect process
                        String newUrl = conn.getHeaderField("Location");
                        String cookies = conn.getHeaderField("Set-Cookie");
                        if(!TextUtils.isEmpty(cookies)) {
                            if(header == null) {
                                header = new Properties();
                            }
                            header.setProperty("Cookie", cookies);
                        }
                        return request(newUrl, method, header, body, timeout, authUser, authPasswd, queryParam);

                    } else {
                        if(RETRY_COUNT <= tryCount) {
                            tryCount = 1;
                            String errorMessage = "";
                            InputStream is = conn.getErrorStream();
                            if(is != null) {
                                errorMessage = InputStreamToString(is);
                            } else {
                                errorMessage = conn.getResponseMessage();
                            }
                            Log.e(TAG + ".RAW", "request - status : " + status + ", conn.getResponseMessage() - " + errorMessage);
                            throw new NetworkException(status, errorMessage);
                        } else {
                            tryCount++;
                            return request(originEndpoint, method, header, body, timeout, authUser, authPasswd, queryParam);
                        }
                    }
                }
            }

            // check Encoding
            InputStream is = conn.getInputStream();
            String encoding = conn.getHeaderField("Content-Encoding");
            if(encoding != null && encoding.trim().compareToIgnoreCase("gzip") == 0) {
                is = new GZIPInputStream(is);
            }

            String response = InputStreamToString(is);
            Log.v(TAG + ".RAW", "request - response : " + response);
            tryCount = 1;

            return response;
        } catch (SocketTimeoutException e) {
            // time out은 공통 처리한다
            tryCount = 1;
            Log.e(TAG + ".RAW", "request timeout : " + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "request time out" : e.getLocalizedMessage()));
            throw new NetworkException(ErrorCode.HTTP_REQUEST_TIMEOUT.code, ErrorCode.HTTP_REQUEST_TIMEOUT.message);
        } catch (IOException e) {
            if(RETRY_COUNT <= tryCount) {
                tryCount = 1;
                Log.e(TAG + ".RAW", "request ; " + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "unknown exception" : e.getLocalizedMessage()));
                throw new NetworkException(ErrorCode.ERROR_CLIENT.code, (TextUtils.isEmpty(e.getLocalizedMessage()) ? "unknown exception" : e.getLocalizedMessage()));
            } else {
                tryCount++;
                return request(originEndpoint, method, header, body, timeout, authUser, authPasswd, queryParam);
            }
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String InputStreamToString(InputStream is) {
        try {
            return IOUtils.toString(is, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void setDefaultSSLSocketFactory(boolean ignore) {

        if (SecureInited) {
            return;
        }
        TrustManager[] wrappedTrustManagers = null;

        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        try {
            tmf.init((KeyStore) null);
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
        }

        TrustManager[] trustManagers = tmf.getTrustManagers();
        final X509TrustManager origTrustmanager = (X509TrustManager) trustManagers[0];

        wrappedTrustManagers = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return origTrustmanager.getAcceptedIssuers();
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                try {
                    origTrustmanager.checkClientTrusted(certs, authType);
                } catch (CertificateException e) {
                    e.printStackTrace();
                }
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                try {
                    origTrustmanager.checkServerTrusted(certs, authType);
                } catch (CertificateException e) {
                    e.printStackTrace();
                }
            }
        } };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, wrappedTrustManagers, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return verifyHostname(hostname);
////                    return true;
//                }
//            });
            SecureInited = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static synchronized boolean verifyHostname(String host) {
        Log.d(TAG + ".SSL", "verifyHostname -- " + host);
        return true;
    }

    public static HttpURLConnection getHttpURLConnection(URL url) throws IOException {

        System.setProperty("http.keepAlive", "false");
        // HTTP
        if (!url.getProtocol().toUpperCase().contains("HTTPS")) {
            return (HttpURLConnection) url.openConnection();
        }

        // Initialize DefaultSSLSocketFactory
        setDefaultSSLSocketFactory(url.getProtocol().toUpperCase().contains("HTTPS"));

        // HTTPs
        HttpsURLConnection httpsConn = null;

        try {
            httpsConn = (HttpsURLConnection) url.openConnection();
            httpsConn.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true);
            httpsConn.setConnectTimeout(TIME_OUT);
            httpsConn.setReadTimeout(TIME_OUT);
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String host, SSLSession session) {
                    return verifyHostname(host);
                }
            });
            return httpsConn;
        } catch (Exception e) {
            try {
                if (httpsConn != null) {
                    httpsConn.disconnect();
                }
            } catch (Exception ignore) {
                ;
            }

            throw e;
        }
    }


    /**
     * upload 할때 multipart body 부분에 Json 데이터가 들어가는 경우 사용할 수 있음
     * @param json json string
     * @return MultipartBuilder instance
     */
    public static MultipartBody makeJsonBody(String json) {
//        return new FormBodyPart("meta-data", new StringBody(json, ContentType.APPLICATION_JSON));
        MultipartBody part = new MultipartBody();
        part.partType = MultipartBody.TYPE_JSON;
        part.name = "meta-data";
        part.content = json;
        part.mimeType = "application/json";
        return part;
    }

    public static MultipartBody makeJsonBody(String json, String fileName) {
//        return new FormBodyPart("meta-data", new StringBody(json, ContentType.APPLICATION_JSON));
        MultipartBody part = new MultipartBody();
        part.partType = MultipartBody.TYPE_JSON;
        part.name = "meta-data";
        part.content = json;
        part.fileName = fileName;
        part.mimeType = "application/json";
        return part;
    }

    public static MultipartBody makeFileBody(String name, File file, String mimeType) {
        MultipartBody part = new MultipartBody();
        part.partType = MultipartBody.TYPE_FILE;
        part.name = name;
        part.file = file;
        part.mimeType = mimeType;
        return part;
    }

    public static MultipartBody makeFileBody(String name, byte[] data, String mimeType, String fileName) {
        MultipartBody part = new MultipartBody();
        part.partType = MultipartBody.TYPE_BINARY;
        part.name = name;
        part.data = data;
        part.fileName = fileName;
        part.mimeType = mimeType;
        return part;
    }

    private static String makeBodyString(Properties body) throws UnsupportedEncodingException {
        String strBody = null;
        StringBuilder sb = new StringBuilder();
        if(body != null) {
            Iterator<Object> itr = body.keySet().iterator();
            while (itr.hasNext()) {
                String key = (String)itr.next();
                String value = body.getProperty(key);
                if(!value.startsWith("[")) {
                    value = URLEncoder.encode(value, "UTF-8");
                }
                sb.append(key).append('=').append(value);
                if(itr.hasNext()) {
                    sb.append('&');
                }
            }
        }
        strBody = sb != null ? sb.toString() : null;
        return strBody;
    }

    public static void trustAllCertForSSL() {
        setDefaultSSLSocketFactory(true);
    }
}
