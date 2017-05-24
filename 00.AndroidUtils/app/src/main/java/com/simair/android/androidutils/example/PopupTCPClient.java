package com.simair.android.androidutils.example;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.simair.android.androidutils.R;
import com.simair.android.androidutils.network.tcp.TCPClient;

/**
 * Created by simair on 17. 5. 24.
 */

public class PopupTCPClient extends ConstraintLayout implements View.OnClickListener, TCPClient.TCPClientListener {
    private final Context context;
    private TCPClient tcpClient;
    private TextView textReceive;
    private EditText editText;
    private EditText editIP;
    private EditText editPort;

    public PopupTCPClient(Context context) {
        this(context, null, 0);
    }

    public PopupTCPClient(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupTCPClient(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.popup_tcp_client, this, true);

        initView();
    }

    private void initView() {
        textReceive = (TextView)findViewById(R.id.textReceive);
        textReceive.setMovementMethod(new ScrollingMovementMethod());
        editText = (EditText)findViewById(R.id.editText);
        findViewById(R.id.btnSend).setOnClickListener(this);

        editIP = (EditText)findViewById(R.id.editIP);
        editPort = (EditText)findViewById(R.id.editPort);
        findViewById(R.id.btnConnect).setOnClickListener(this);
    }

    private void addMessage(String message) {
        textReceive.append(message + "\n");
        int scroll = textReceive.getLayout().getLineTop(textReceive.getLineCount()) - textReceive.getHeight();
        if(scroll > 0) {
            textReceive.scrollTo(0, scroll);
        } else {
            textReceive.scrollTo(0,0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                if(!TextUtils.isEmpty(editText.getText())) {
                    String data = editText.getText().toString();
                    tcpClient.send(data);
                }
                break;
            case R.id.btnConnect:
                if(!TextUtils.isEmpty(editIP.getText()) && !TextUtils.isEmpty(editPort.getText())) {
                    tcpClient = new TCPClient(true, editIP.getText().toString(), Integer.parseInt(editPort.getText().toString()));
                    tcpClient.startClient(this);
                }
                break;
        }
    }

    @Override
    public void onConnected(String host) {
        Toast.makeText(context, "onConnected", Toast.LENGTH_SHORT).show();
        addMessage("connected : " + host);
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(context, "onDisconnected", Toast.LENGTH_SHORT).show();
        addMessage("disconnected");
    }

    @Override
    public void onReceived(String data) {
        addMessage(data);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        addMessage(message);
    }

    @Override
    public void onSendOK() {
//        Toast.makeText(context, "onSendOK", Toast.LENGTH_SHORT).show();
    }
}
