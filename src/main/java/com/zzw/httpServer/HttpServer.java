package com.zzw.httpServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public HttpServer(int port) throws IOException {
        if(port<1||port>65535) throw new MyRuntimeException("端口异常!");

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器已经启动 正在监听"+port+"端口");

        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("接收到客户端请求:"+clientSocket.getPort());
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(8383);
    }
}
