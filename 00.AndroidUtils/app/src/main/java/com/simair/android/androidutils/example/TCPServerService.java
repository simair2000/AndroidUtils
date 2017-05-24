package com.simair.android.androidutils.example;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.R;
import com.simair.android.androidutils.network.NetworkUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServerService extends Service {

    public static int port = 0;
    private static final String TAG = TCPServerService.class.getSimpleName();
    public static Context context = null;
    private ExecutorService executorService;
    private ServerSocketChannel serverSocketChannel;
    private List<Client> connections = new Vector<>();
    private String ip;
    private static TCPServerListener listener;

    public interface TCPServerListener {
        void onStarted(String ip, int port);
        void onStoped();
        void onClientConnected(String ip, int total);
    }

    public static void start(Context c, TCPServerListener l) {
        context = c;
        listener = l;
        Intent i = new Intent(context, TCPServerService.class);
        context.startService(i);
    }

    public static void stop() {
        Intent i = new Intent(context, TCPServerService.class);
        context.stopService(i);
    }

    public TCPServerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTCPServer();
        return START_STICKY;
    }

    private void startTCPServer() {
        NetworkUtil.getLocalIpAddress(context, new Command.CommandListener() {
            @Override
            public void onSuccess(Command command, Bundle data) {
                ip = data.getString("ip");
                startServer();
            }

            @Override
            public void onFail(Command command, int errorCode, String errorMessage) {
                Toast.makeText(context, "네트워크 오류!", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });

//        ip = NetworkUtil.getIPAddress(true);
//        startServer();
    }

    private void startServer() {
        // thread pool 생성
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(true);    // blocking mode

            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            port = serverSocketChannel.socket().getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
            if(serverSocketChannel.isOpen()) {
                stopServer();
            }
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showNotification(connections.size());
                if(listener != null) {
                    listener.onStarted(ip, port);
                }

                while (true) {
                    try {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        Client client = new Client(socketChannel);
                        connections.add(client);
                        showNotification(connections.size());
                        if(listener != null) {
                            listener.onClientConnected(client.socketChannel.socket().getRemoteSocketAddress().toString(), connections.size());
                        }
                    } catch (Exception e) {
                        if(serverSocketChannel.isOpen()) {
                            stopServer();
                        }
                        break;
                    }
                }
            }
        };

        executorService.submit(runnable);
    }

    private void stopServer() {
        try {
            Iterator<Client> itr = connections.iterator();
            while (itr.hasNext()) {
                Client client = itr.next();
                client.socketChannel.close();
                itr.remove();
            }

            if(serverSocketChannel != null && serverSocketChannel.isOpen()) {
                serverSocketChannel.close();
            }

            if(executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(listener != null) {
            listener.onStoped();
        }
        stopForeground(true);
//        stopSelf();
    }

    private void showNotification(int count) {
        Notification notification = new Notification.Builder(context)
                .setContentTitle("TCP Server Started")
                .setContentText(ip + ":" + port + " [" + count + "]")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class Client {

        private final SocketChannel socketChannel;

        public Client(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
            receive();
        }

        private void receive() {
            Runnable receiveTask = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                            int byteCount = socketChannel.read(byteBuffer);
                            if(byteCount == -1) {
                                throw new IOException();
                            }
                            String msg = "[요청 처리: " + socketChannel.socket().getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
                            Log.d(TAG, msg);

                            byteBuffer.flip();
                            Charset charset = Charset.forName("UTF-8");
                            String data = charset.decode(byteBuffer).toString();
                            // 연결된 모든 client 에게 수신된 message를 보내준다 채팅처럼
                            for(Client client : connections) {
                                client.send(data);
                            }
                        } catch (Exception e) {
                            connections.remove(Client.this);
                            try {
                                socketChannel.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            };
            executorService.submit(receiveTask);
        }

        private void send(final String data) {
            Runnable sendTask = new Runnable() {
                @Override
                public void run() {
                    try {
                        Charset charset = Charset.forName("UTF-8");
                        ByteBuffer byteBuffer = charset.encode(data);
                        socketChannel.write(byteBuffer);
                    } catch (Exception e) {
                        String msg = "[클라이언트 통신 안됨: " + socketChannel.socket().getRemoteSocketAddress() + ": "
                                + Thread.currentThread().getName() + "]";
                        Log.e(TAG, msg);
                        connections.remove(Client.this);
                        try {
                            socketChannel.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            };

            executorService.submit(sendTask);
        }
    }
}
