package com.zzw.httpServer;
import java.io.*;
import java.lang.String;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
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

    //验证无效请求
    private void validate(Socket clientSocket) throws IOException {
        //获取输出流
        OutputStream outputStream = clientSocket.getOutputStream();
        //获取输入流
        InputStream inputStream = clientSocket.getInputStream();
        if(inputStream.available()==0){
            toResponse(outputStream,200,"OK","text/html","<h1>OK</h1>");
            return;
        }
    }

    //响应客户端的请求
    public void responseToClient(Socket clientSocket) throws IOException, InterruptedException {
        //获取输出流
        OutputStream outputStream = clientSocket.getOutputStream();

        //获取输入流
        InputStream inputStream = clientSocket.getInputStream();

        //处理无效请求
        validate(clientSocket);

        //处理无效请求
        int availableDateLen = inputStream.available();
        System.out.println("客户端的请求数据长度:"+availableDateLen);

        //获取请求文档
        byte[] readBuffer = new byte[availableDateLen];

        inputStream.read(readBuffer);

        String s = new String(readBuffer);

        String[] lineArray = s.split("\r\n");
        String requestLine = lineArray[0];

        String[] requestLineArray = requestLine.split(" ");
        String requestURL = requestLineArray[1];


        //如果请求的路径是其他静态路径则进行处理
        if(requestURL.equals("/favicon.ico")){
            toResponse(outputStream,200,"OK","text/html","favicon.ico");
            return;
        }

        //验证请求路径 如果是默认路径 也就是 '/'则去index页面 否则 去请求的对应页面
        requestURL = requestURL.equals("/")? "index.html" :requestURL.substring(1);

        //去服务器内部的resource文件夹 找 对应文件名的文件
        URL resourcePath = getClass().getClassLoader().getResource(requestURL);

        //如果文件不存在 返回404
        if(resourcePath==null){
            toResponse(outputStream,404,"Not Found","text/html","<h1>404 File Not Found!</h1>");
            return;
        }

        //找得到就 去用bufferInputStream读出来
        byte[] temp = new byte[2048];
        int len = 0;//记录每次读取的有效字节个数

        //然后把读到的内容放到responseDate中
        String responseDate ="";

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(resourcePath.getPath()));){
            while ((len = bis.read(temp))!=-1){
                responseDate += new String(temp, 0, len);
            }
            bis.read(temp);
        }

        //最后返回
        toResponse(outputStream,200,"OK","text/html",responseDate);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer httpServer = new HttpServer(8383);
    }
}
