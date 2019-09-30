package com.zjicm.calculation;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    //定义相关的参数,端口,存储Socket连接的集合,ServerSocket对象以及线程池
    private static final int PORT = 12345;
    private List<Socket> mList = new ArrayList<>();
    private ServerSocket serverSocket = null;
    private ExecutorService myExecutorService = null;
    private Socket client = null;
    private Calculator calculator;


    public static void main(String[] args) throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println("服务器IP地址为：" + address.getHostAddress());
        new Server();
    }

    private Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            //创建线程池
            myExecutorService = Executors.newCachedThreadPool();
            System.out.println("服务器启动完毕" + "\n");
            while (true) {
                client = serverSocket.accept();
                mList.add(client);
                myExecutorService.execute(new Service(client));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Service implements Runnable {
        private Socket socket = null;
        private BufferedReader in = null;
        private String message = "";
        private PrintWriter pout = null;

        private Service(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                message = "用户:" + this.socket.getInetAddress() + "加入," + "当前在线人数:" + mList.size() + "\n";
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if ((message = in.readLine()) != null) {
                        if (message.equals("")) {

                        } else if (message.equals("bye")) {
                            mList.remove(socket);
                            in.close();
                            socket.close();
                            message = "用户:" + socket.getInetAddress() + "退出," + "当前在线人数:" + mList.size();
                            System.out.println(message);
                            break;
                        } else {
                            System.out.println("用户:" + socket.getInetAddress() + "请求计算表达式: " + message);
                            this.sendMessageToClient();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //为连接上服务端的每个客户端发送信息
        private void sendMessageToClient() {
            try {
                if (socket.isConnected()) {
                    if (!socket.isOutputShutdown()) {
                        // 将缓冲区的数据强制输出，用于清空缓冲区，若直接调用close()方法，则可能会丢失缓冲区的数据。所以通俗来讲它起到的是刷新的作用。flush();
                        pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                        calculator = new Calculator(message);

                        pout.println("    服务器返回结果"+ ":" + calculator.getResult());
                        System.out.println("服务器返回结果至" + socket.getInetAddress() + ":" + calculator.getResult() + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}