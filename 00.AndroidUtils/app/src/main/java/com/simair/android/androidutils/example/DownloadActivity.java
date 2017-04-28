package com.simair.android.androidutils.example;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.ErrorCode;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.Utils;
import com.simair.android.androidutils.network.NetworkException;
import com.simair.android.androidutils.network.http.Network;
import com.simair.android.androidutils.ui.CustomPopup;

import org.json.JSONException;

import java.io.File;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener, Command.DownloadListener, Command.CommandListener {

    private EditText editText;
    private Command commandDownload;
    private ProgressBar progress;
    private long total = 0;
    private TextView textProgress;
    private long currentRead = 0;
    private int recvIndex = 0;
    private TextView textTime;
    private long startTime;
    private Handler handler = new Handler();
    private Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            long time = SystemClock.uptimeMillis() - startTime;
            textTime.setText(Utils.convertMillisecondsToFormat(time));
            handler.postDelayed(this, 10);
        }
    };
    private CustomPopup popup;

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

        editText.setText("http://221.140.57.225:9000/smartguard/papais_the_plan.mp4");    // 1.4GB
//        editText.setText("http://221.140.57.225:9000/smartguard/yours.mp4");    // 58.6MB
//        editText.setText("http://221.140.57.225:9000/smartguard/KTSmartGuard.zip");    // 9.6MB
//        editText.setText("http://221.140.57.225:9000/smartguard/PKI.pptx");    // 43.3KB
//        editText.setText("http://221.140.57.225:9000/smartguard/test.html");    // 3.0KB
//        editText.setText("http://221.140.57.225:9000/smartguard/Wikiki_CD1.mp4");
//        editText.setText("http://221.140.57.225:9000/smartguard/Wikiki_CD2.mp4");

        textTime = (TextView) findViewById(R.id.textTime);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDown:
                commandDownload = new Command() {
                    @Override
                    public void doAction(Bundle data) throws NetworkException, JSONException {
                        boolean overwrite = data.getBoolean("overwrite", false);
                        String url = editText.getText().toString();
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        String resultPath = Network.download(url, path.getAbsolutePath(), null, this, overwrite);
                        data.putString("path", resultPath);
                    }
                }.setOnCommandListener(this).setOnDownloadListener(this).start();
                break;
        }
    }

    @Override
    public void onDownloadStart(Command command, String url, long total) {
        if(command == commandDownload) {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(timerTask, 0);
            this.total = total;
            this.currentRead = 0;
            textProgress.setText("0%\n0/" + Utils.getFileSize(total));
            progress.setMax(100);
        }
    }

    @Override
    public void onDownloading(Command command, String url, long read) {
        if(command == commandDownload) {
//            Log.w("simair_recv", "recv message : " + recvIndex++);
//            sums += read;
            currentRead = read;
            long percent = (currentRead * 100) / total;
            progress.setProgress((int) percent);
            textProgress.setText(percent + "%\n" + Utils.getFileSize(currentRead) + "/" + Utils.getFileSize(total));
        }
    }

    @Override
    public void onDownloadEnd(Command command, String url, long total) {
        if(command == commandDownload) {
            handler.removeCallbacks(timerTask);
//            textProgress.setText("100%\n" + currentRead + "/" + total);
//            progress.setProgress(100);
        }
    }

    @Override
    public void onSuccess(Command command, Bundle data) {
        if(command == commandDownload) {
            Snackbar.make(this.getCurrentFocus(), "download complete!!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFail(final Command command, int errorCode, String errorMessage) {
        if(command == commandDownload) {
            if(errorCode == ErrorCode.FILE_EXIST.code) {
                // 파일이 이미 존재함
                CustomPopup.hideDialog(popup);
                popup = new CustomPopup(this).build(R.layout.popup_test, new CustomPopup.CustomPopupListener() {
                    @Override
                    public void onButtonClicked(View button) {
                        CustomPopup.hideDialog(popup);
                        if(button.getId() == R.id.btnOK) {
                            commandDownload.getData().putBoolean("overwrite", true);
                            commandDownload.start();
                        }
                    }
                }, R.id.btnOK, R.id.btnCancel).useSound(true).show();
                Dialog view = popup.getDialogView();
                ((TextView)view.findViewById(R.id.textView6)).setText(errorMessage);
                ((TextView)view.findViewById(R.id.textView7)).setText("덮어쓰시겠습니까?");
                view.findViewById(R.id.textView8).setVisibility(View.GONE);
                ((Button)view.findViewById(R.id.btnOK)).setText("네");
                ((Button)view.findViewById(R.id.btnCancel)).setText("아니오");
            } else {
                textProgress.setText("Download failed!!");
                progress.setProgress(0);
                Snackbar.make(this.getCurrentFocus(), "download fail!!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
