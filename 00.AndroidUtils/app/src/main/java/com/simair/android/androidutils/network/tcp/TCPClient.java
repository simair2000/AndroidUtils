package com.simair.android.androidutils.network.tcp;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created by simair on 17. 5. 24.
 */

public class TCPClient {

    private static final int RECV_BUFF_SIZE = 1024 * 8;
    private final boolean isBlock;
    private final String hostName;
    private final int port;
    private SocketChannel socketChannel;
    private  TCPClientListener listener;
    private TCPHandler handler = new TCPHandler(this);

    public interface TCPClientListener {
        void onConnected(String host);
        void onDisconnected();
        void onReceived(String data);
        void onError(String message);
        void onSendOK();
    }

    public TCPClient(boolean isBlock, String hostName, int port) {
        this.isBlock = isBlock;
        this.hostName = hostName;
        this.port = port;
    }

    public TCPClient startClient(TCPClientListener l) {
        listener = l;
        new Thread() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(isBlock);
                    socketChannel.connect(new InetSocketAddress(hostName, port));
                    Message msg = handler.obtainMessage(WHAT_CONNECTED, socketChannel.socket().getRemoteSocketAddress().toString());
                    msg.sendToTarget();
                } catch (Exception e) {
                    procException(e);
                    if(socketChannel.isOpen()) {
                        stopClient();
                    }
                    return;
                }
                receive();
            }
        }.start();
        return this;
    }

    public void send(final String data) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Charset charset = Charset.forName("UTF-8");
                    ByteBuffer byteBuffer = charset.encode(data);
                    socketChannel.write(byteBuffer);
                    Message msg = handler.obtainMessage(WHAT_SEND_OK);
                    msg.sendToTarget();
                } catch (Exception e) {
                    procException(e);
                    stopClient();
                }
            }
        }.start();
    }

    private void receive() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(RECV_BUFF_SIZE);
                int byteCount = socketChannel.read(byteBuffer);
                if(byteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip();
                Charset charset = Charset.forName("UTF-8");
                String data = charset.decode(byteBuffer).toString();
                Message msg = handler.obtainMessage(WHAT_RECEIVED, data);
                msg.sendToTarget();
            } catch (Exception e) {
                procException(e);
                stopClient();
                break;
            }
        }
    }

    public void stopClient() {
        try {
            if(socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
            Message msg = handler.obtainMessage(WHAT_DISCONNECTED);
            msg.sendToTarget();
        } catch (Exception e) {

        }
    }

    private void procException(Exception e) {
        Message msg = handler.obtainMessage(WHAT_ERROR, TextUtils.isEmpty(e.getLocalizedMessage()) ? "unknown exception" : e.getLocalizedMessage());
        msg.sendToTarget();
    }

    private static final int WHAT_CONNECTED = 1;
    private static final int WHAT_DISCONNECTED = 2;
    private static final int WHAT_RECEIVED = 3;
    private static final int WHAT_ERROR = 4;
    private static final int WHAT_SEND_OK = 5;

    public static class TCPHandler extends Handler {
        WeakReference<TCPClient> handerObject;

        TCPHandler(TCPClient handlerObject) {
            this.handerObject = new WeakReference<>(handlerObject);
        }

        @Override
        public void handleMessage(Message msg) {
            TCPClient tcpClient = handerObject.get();
            if(tcpClient == null) {
                return;
            }

            switch (msg.what) {
                case WHAT_CONNECTED:
                    if(tcpClient.listener != null) {
                        String host = (String) msg.obj;
                        tcpClient.listener.onConnected(host);
                    }
                    break;
                case WHAT_DISCONNECTED:
                    if(tcpClient.listener != null) {
                        tcpClient.listener.onDisconnected();
                    }
                    break;
                case WHAT_RECEIVED:
                    if(tcpClient.handler != null) {
                        String data = (String)msg.obj;
                        tcpClient.listener.onReceived(data);
                    }
                    break;
                case WHAT_ERROR:
                    if(tcpClient.listener != null) {
                        String message = (String) msg.obj;
                        tcpClient.listener.onError(message);
                    }
                    break;
                case WHAT_SEND_OK:
                    if(tcpClient.listener != null) {
                        tcpClient.listener.onSendOK();
                    }
                    break;
            }
        }
    }
}
