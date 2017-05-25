package com.simair.android.androidutils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.simair.android.androidutils.network.NetworkException;

import org.json.JSONException;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by simair on 16. 11. 17.
 */

public abstract class Command implements Serializable {
    private String TAG = this.getClass().getSimpleName();
    private Command instance = this;
    private Bundle data;
    private CommandListener listener;
    private Context context;
    private boolean showWait;
    private ProgressDialog popup = null;
    private DownloadListener downListener;

    public Command() {
        showWait = false;
        data = new Bundle();
    }

    public abstract void doAction(Bundle data) throws NetworkException, JSONException;

    public interface CommandListener extends Serializable {
        void onSuccess(Command command, Bundle data);
        void onFail(Command command, int errorCode, String errorMessage);
    }

    public interface DownloadListener {
        void onDownloadStart(Command command, String url, long total);
        void onDownloading(Command command, String url, long read);
        void onDownloadEnd(Command command, String url, long total);
    }

    public Command setOnCommandListener(CommandListener l) {
        this.listener = l;
        return this;
    }

    public Command setOnDownloadListener(DownloadListener l) {
        this.downListener = l;
        return this;
    }

    public Command setData(Bundle data) {
        this.data = data;
        return this;
    }

    public Command showWaitDialog(Context context) {
        this.context = context;
        this.showWait = true;
        popup = new ProgressDialog(context);
        popup.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        popup.setMessage("loading...");
        return this;
    }

    public void hideWaitDialog() {
        if(popup != null) {
            popup.dismiss();
            popup = null;
        }
    }

    public Bundle getData() {
        return this.data;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    public Command start(long delay) {
        if(showWait) {
            popup.show();
            popup.setCancelable(false);
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    doAction(data);
                    handler.sendEmptyMessage(WHAT_SUCCESS);
                } catch (NetworkException e) {
                    Log.w(TAG, (TextUtils.isEmpty(e.message) ? "unknown exception - " + e.messageResId : e.message));
                    Message msg = handler.obtainMessage(WHAT_FAIL, e.code, e.messageResId, (TextUtils.isEmpty(e.message) ? "unknown exception - " + e.messageResId : e.message));
                    msg.sendToTarget();
                } catch (JSONException e) {
                    Log.w(TAG, e.getLocalizedMessage());
                    Message msg = handler.obtainMessage(WHAT_FAIL, ErrorCode.ERROR_JSON.code, 0, "invalid response");
                    msg.sendToTarget();
                } finally {
                    if(popup != null) {
                        popup.dismiss();
                        popup = null;
                    }
                }
            }
        }, delay);
        return this;
    }

    public Command start() {
        if(showWait) {
            popup.show();
            popup.setCancelable(false);
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    doAction(data);
                    handler.sendEmptyMessage(WHAT_SUCCESS);
                } catch (NetworkException e) {
                    Log.w(TAG, (TextUtils.isEmpty(e.message) ? "unknown exception - " + e.messageResId : e.message));
                    Message msg = handler.obtainMessage(WHAT_FAIL, e.code, e.messageResId, (TextUtils.isEmpty(e.message) ? "unknown exception - " + e.messageResId : e.message));
                    msg.sendToTarget();
                } catch (JSONException e) {
                    Log.w(TAG, e.getLocalizedMessage());
                    Message msg = handler.obtainMessage(WHAT_FAIL, ErrorCode.ERROR_JSON.code, 0, "invalid response");
                    msg.sendToTarget();
                } finally {
                    if(popup != null) {
                        popup.dismiss();
                        popup = null;
                    }
                }
            }
        }).start();
        return this;
    }

    private static final int WHAT_SUCCESS = 1;
    private static final int WHAT_FAIL = 2;
    public static final int WHAT_DOWN_START = 3;
    public static final int WHAT_DOWNLOADING = 4;
    public static final int WHAT_DOWN_END = 5;
    private CommandHandler handler = new CommandHandler(this);

    public static class CommandHandler extends Handler {
        WeakReference<Command> handlerObj;

        CommandHandler(Command handlerObj) {
            this.handlerObj = new WeakReference<Command>(handlerObj);
        }

        @Override
        public void handleMessage(Message msg) {
            Command command = handlerObj.get();
            if(command == null) return;
            switch(msg.what) {
                case Command.WHAT_SUCCESS:
                    if(command.listener != null) {
                        command.listener.onSuccess(command.instance, command.data);
                    }
                    break;
                case Command.WHAT_FAIL:
                    if(msg.arg1 == ErrorCode.ERROR_NETWORK_UNAVAILABLE.code ||
                            msg.arg1 == ErrorCode.HTTP_REQUEST_TIMEOUT.code) {
                        // 네트워크에러는 공통으로 Toast 처리한다.
//                        Toast.makeText(context, msg.arg2, Toast.LENGTH_SHORT);
                    }
                    if(command.listener != null) {
                        command.listener.onFail(command.instance, msg.arg1, (String)msg.obj);
                    }
                    break;
                case Command.WHAT_DOWN_START:
                    if(command.downListener != null) {
                        Bundle extra = (Bundle) msg.obj;
                        command.downListener.onDownloadStart(command.instance, extra.getString("url"), extra.getLong("total"));
                    }
                    break;
                case Command.WHAT_DOWNLOADING:
                    if(command.downListener != null) {
                        Bundle extra = (Bundle) msg.obj;
                        command.downListener.onDownloading(command.instance, extra.getString("url"), extra.getLong("read"));
                    }
                    break;
                case Command.WHAT_DOWN_END:
                    if(command.downListener != null) {
                        Bundle extra = (Bundle) msg.obj;
                        command.downListener.onDownloadEnd(command.instance, extra.getString("url"), extra.getLong("total"));
                    }
                    break;
            }
        }
    }
}
