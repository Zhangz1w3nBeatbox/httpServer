package com.zzw.httpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class HttpServer {

    public HttpServer(int port) throws IOException, InterruptedException {
        if(port<1||port>65535) throw new MyRuntimeException("端口异常!");

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器已经启动 正在监听"+port+"端口");

        while(true){

            Socket clientSocket = serverSocket.accept();

            System.out.println("接收到客户端请求:"+clientSocket.getPort());

            if(clientSocket!=null&&!clientSocket.isClosed()){

                responseToClient(clientSocket);

            }

        }


    }


    //响应客户端的请求
    public void responseToClient(Socket clientSocket) throws IOException, InterruptedException {

        //模拟处理 耗时
        Thread.sleep(3000);

        OutputStream outputStream = clientSocket.getOutputStream();

        //响应头信息

        //状态行 版本号 状态码
        outputStream.write("HTTP/1.0 200 OK\r\n".getBytes());

        //首部行
        outputStream.write("Server:HttpServer/1.0\r\n".getBytes());
        outputStream.write(("Date:"+(new Date()).toString()+"\r\n").getBytes());
        outputStream.write("Content-Type: text/html; charset=UTF-8\r\n".getBytes());


        outputStream.write("\r\n".getBytes());

        //响应-实体体
        outputStream.write("<h1>Welcome Beatboxer!</h1>".getBytes());


        outputStream.flush();
        outputStream.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer httpServer = new HttpServer(8383);
    }
}
