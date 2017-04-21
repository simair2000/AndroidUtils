package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;

import org.json.JSONException;

import java.io.File;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener, Command.DownloadListener, Command.CommandListener {

    private EditText editText;
    private Command commandDownload;
    private ProgressBar progress;
    private int total = 0;
    private TextView textProgress;
    private int sums = 0;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, DownloadActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
    }

    private void initView() {
        findViewById(R.id.btnDown).setOnClickListener(this);
        editText = (EditText)findViewById(R.id.editText);
        progress = (ProgressBar)findViewById(R.id.progressBar);
        textProgress = (TextView)findViewById(R.id.textProgress);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDown:
                commandDownload = new Command() {
                    @Override
                    public void doAction(Bundle data) throws NetworkException, JSONException {
                        String url = editText.getText().toString();
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        String resultPath = Network.download(url, path.getAbsolutePath(), "test.mp4", getHandler());
                        data.putString("path", resultPath);
                    }
                }.setOnCommandListener(this).setOnDownloadListener(this).start();
                break;
        }
    }

    @Override
    public void onDownloadStart(String url, int total) {
        this.total = total / 1000;
        this.sums = 0;
        textProgress.setText("0% 0/" + this.total);
        progress.setMax(100);
    }

    @Override
    public void onDownloading(String url, int read) {
        sums += (read / 1000);
        int percent = (sums * 100) / total;
        progress.setProgress(percent);
        textProgress.setText(percent + "% " + sums + "/" + total);
    }

    @Override
    public void onDownloadEnd(String url) {
        textProgress.setText("100%");
        progress.setProgress(100);
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        Snackbar.make(this.getCurrentFocus(), "download complete!!", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFail(Command command, int errorCode, String errorMessage) {
        Snackbar.make(this.getCurrentFocus(), "download fail!!", Snackbar.LENGTH_SHORT).show();
    }
}
