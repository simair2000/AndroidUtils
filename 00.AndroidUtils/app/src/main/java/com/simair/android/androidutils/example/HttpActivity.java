package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;

import org.json.JSONException;

public class HttpActivity extends AppCompatActivity implements TextView.OnEditorActionListener, Command.CommandListener {

    private EditText editTextURL;
    private WebView webView;
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };
    private Command commandGet;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, HttpActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        initView();
    }

    private void initView() {
        editTextURL = (EditText)findViewById(R.id.editTextURL);
        editTextURL.setOnEditorActionListener(this);
        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(webViewClient);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        Utils.hideKeyboard(this);
        if(!TextUtils.isEmpty(textView.getText())) {
            final String url = textView.getText().toString();
            gotoURL(url);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void gotoURL(final String url) {
        commandGet = new Command() {
            @Override
            public void doAction(Bundle data) throws NetworkException, JSONException {
                String protocol = null;
                String host = null;

                if(url.contains("://")) {
                    String[] parts = url.split("://");
                    protocol = parts[0];
                    host = parts[1];
                } else {
                    url.replaceAll("//", "");
                    protocol = "http";
                    host = url;
                }

                final String page = Network.get(protocol.trim(), host.trim(), "", null, null);
                data.putString("page", page);
                data.putString("url", protocol.trim() + "://" + host.trim());
            }
        }.showWaitDialog(this).setOnCommandListener(this).start();
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        if(command == commandGet) {
            editTextURL.setText(data.getString("url"));
            final String page = data.getString("page");
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadData(page, "text/html; charset=utf-8", "utf-8");
                }
            });
        }
    }

    @Override
    public void onFail(Command command, int errorCode, final String errorMessage) {
        if(command == commandGet) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadData(errorMessage, "text/html; charset=utf-8", "utf-8");
                }
            });
        }
    }
}
