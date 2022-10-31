package com.zzw.httpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    public HttpServer(int port) throws IOException, InterruptedException {
        if(port<1||port>65535) throw new MyRuntimeException("端口异常!");

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器已经启动 正在监听"+port+"端口");

        //使用线程池 处理并发的请求

        ExecutorService threadPool = Executors.newFixedThreadPool(50);

        while(true){

            Socket clientSocket = serverSocket.accept();

            System.out.println("接收到客户端请求:"+clientSocket.getPort());

            if(clientSocket!=null&&!clientSocket.isClosed()){

                //构建任务
               Runnable work =()->{
                   try {
                       responseToClient(clientSocket);
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }
               };
                //提价任务
               threadPool.submit(work);

            }

        }


    }


    //响应客户端的请求
    public void responseToClient(Socket clientSocket) throws IOException, InterruptedException {

        //模拟处理 耗时
        //Thread.sleep(3000);

        //获取输出流
        OutputStream outputStream = clientSocket.getOutputStream();
        //获取输入流
        InputStream inputStream = clientSocket.getInputStream();



        if(inputStream.available()==0){
            //响应头信息

            //状态行 版本号 状态码
            outputStream.write("HTTP/1.0 200 OK\r\n".getBytes());

            //首部行
            outputStream.write("Server:HttpServer/1.0\r\n".getBytes());
            outputStream.write(("Date:"+(new Date()).toString()+"\r\n").getBytes());
            outputStream.write("Content-Type: text/html; charset=UTF-8\r\n".getBytes());


            outputStream.write("\r\n".getBytes());

            //响应-实体体
            outputStream.write("<h1>OK</h1>".getBytes());


            outputStream.flush();
            outputStream.close();

            return;
        }

        //处理无效请求
        System.out.println("客户端的请求数据长度:"+inputStream.available());

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
