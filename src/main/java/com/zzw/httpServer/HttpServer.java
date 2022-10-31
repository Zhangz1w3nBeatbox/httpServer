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

    //封装response对象
    public void toResponse(OutputStream outputStream,int resCode,String resDes,String contentType,String resData) throws IOException {
        //响应头信息

        //状态行 版本号 状态码
        outputStream.write(("HTTP/1.1 "+resCode+" "+resDes+"\r\n").getBytes());

        //首部行
        outputStream.write("Server:HttpServer/1.1\r\n".getBytes());
        outputStream.write(("Date:"+(new Date())+"\r\n").getBytes());
        outputStream.write(("Content-Type: "+contentType+"; charset=UTF-8\r\n").getBytes());


        outputStream.write("\r\n".getBytes());

        //响应-实体体
        outputStream.write(resData.getBytes());


        outputStream.flush();
        outputStream.close();
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
            toResponse(outputStream,200,"OK","text/html","<h1>OK</h1>");
            return;
        }

        //处理无效请求
        System.out.println("客户端的请求数据长度:"+inputStream.available());

        toResponse(outputStream,200,"OK","text/html","<h1>主人欢迎回来!</h1>");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer httpServer = new HttpServer(8383);
    }
}
