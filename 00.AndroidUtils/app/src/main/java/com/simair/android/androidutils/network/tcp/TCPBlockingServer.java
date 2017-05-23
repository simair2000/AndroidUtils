package com.simair.android.androidutils.network.tcp;

import android.util.Log;

import com.simair.android.androidutils.Command;
import com.simair.android.androidutils.ErrorCode;
import com.simair.android.androidutils.network.NetworkException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by simair on 17. 4. 28.<br>
 * TCP Blocking server<br>
 * 스레드풀(ExecutorService)을 사용하여 과도한 쓰레드를 발생시키지 않도록 한다<br>
 * 참조 <a href="http://palpit.tistory.com/644">http://palpit.tistory.com/644</a>
 */
public class TCPBlockingServer {

    private static final String TAG = TCPBlockingServer.class.getSimpleName();
    private final Command command;
    private ServerSocketChannel serverSocketChannel = null;
    private int serverPort;
    private ExecutorService executorService;
    List<ConnectedClient> connectedClientList = new Vector<ConnectedClient>();

    private Runnable acceptTask = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    SocketChannel clientChannel = serverSocketChannel.accept();
                    Log.d(TAG, "[accepted] : " +clientChannel.socket().getRemoteSocketAddress());
//                    command.tcpServerAccepted(clientChannel.getRemoteAddress().toString());
                    ConnectedClient client = new ConnectedClient(clientChannel);
                    connectedClientList.add(client);
                    Log.d(TAG, "[연결 갯수 : " + connectedClientList.size() + "]");
                } catch (IOException e) {
                    e.printStackTrace();if(serverSocketChannel.isOpen()) {
                        try {
                            stopServer();
                        } catch (NetworkException e1) {
                            e1.printStackTrace();
                        }
                    }
                    break;
                }
            }
        }
    };

    public TCPBlockingServer(Command command) {
        this.command = command;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 현재 서버에 접속된 client list를 반환한다
     * @return
     */
    public List<ConnectedClient> getConnectedClientList() {
        return connectedClientList;
    }

    /**
     * 서버를 start한다
     * @param port 서버 port number
     * @return 서버의 public IP address를 반환
     * @throws NetworkException
     */
    public String startServer(int port) throws NetworkException {
//        String ip = NetworkUtil.getLocalIpAddress();
//        Log.d(TAG, ip);

        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
            if(serverSocketChannel.isOpen()) {
                stopServer();
            }
            throw new NetworkException(ErrorCode.ERROR_TCP_START_SERVER.code, e.getLocalizedMessage());
        }
        executorService.submit(acceptTask);

        return null;
    }

    private void stopServer() throws NetworkException {
        try {
            Iterator<ConnectedClient> itr = connectedClientList.iterator();
            while (itr.hasNext()) {
                ConnectedClient client = itr.next();
                client.channel.close();
                itr.remove();
            }
            if(serverSocketChannel != null && serverSocketChannel.isOpen()) {
                serverSocketChannel.close();
            }
            if(executorService != null && executorService.isShutdown()) {
                executorService.shutdown();
            }
            Log.d(TAG, "[서버 종료]");
        } catch (IOException e) {
            e.printStackTrace();
            throw new NetworkException(ErrorCode.ERROR_TCP_STOP_SERVER.code, e.getLocalizedMessage());
        }
    }

    /**
     * 연결된 client에게 data를 전송한다
     * @param client data를 전달할 client
     * @param data 전달할 data
     */
    public void send(ConnectedClient client, String data) {

    }

    /**
     * 연결된 client list에게 data를 전송한다
     * @param clients 연결된 client list
     * @param data 전송할 data
     */
    public void send(List<ConnectedClient> clients, String data) {

    }

    /**
     * 연결된 모든 client 에게 data를 전송한다
     * @param data 전송할 data
     */
    public void sendAll(String data) {

    }

    public class ConnectedClient {

        public SocketChannel channel;
        private Runnable receiveTask = new Runnable() {

            @Override
            public void run() {
                while (true) {

                }
            }
        };

        public ConnectedClient(SocketChannel clientChannel) {
            channel = clientChannel;
            executorService.submit(receiveTask);
        }
    }
}
