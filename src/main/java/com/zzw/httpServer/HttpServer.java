package com.zzw.httpServer;
import com.zzw.httpServer.Entity.HeaderLine;
import com.zzw.httpServer.Entity.Response;
import com.zzw.httpServer.Entity.StatusLine;

import java.io.*;
import java.lang.String;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    //单例 servletPool
    private Map<String,Servlet> servletPool = new HashMap();

    //获取servlet方法
    private synchronized Servlet getServlet(String servletName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // 把 login 变为  LoginServlet类  通过反射 创建对象

        servletName = "com.zzw.httpServer."+servletName.substring(0,1).toUpperCase()+servletName.substring(1)+"Servlet";

        //System.out.println(servletName);

        if(servletPool.containsKey(servletName)) return servletPool.get(servletName);

        //System.out.println(servletName);

        //通过反射获取servlet类
        Class aClass = Class.forName(servletName);

        Servlet servlet  = (Servlet) aClass.newInstance();

        servletPool.put(servletName,servlet);

        return servlet;
    }

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
    public void toResponse(OutputStream outputStream,Response response) throws IOException {
        //响应头信息

        //
        String statusLined = response.getStatusLine().toString();
        outputStream.write(statusLined.getBytes());

        String headLine  = response.getHeaderLine().toString();
        outputStream.write(headLine.getBytes());

        //换行
        outputStream.write("\r\n".getBytes());

        //响应-实体体
        outputStream.write(response.getEntityBody());

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
            transferResponse(outputStream,"HTTP/1.1",200,"OK","text/html","<h1>OK</h1>".getBytes());
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

        //获取所有请求文档
        String s = new String(readBuffer);

        String[] lineArray =s.split("\r\n");

        for(String t:lineArray){
            System.out.println(t);
        }

        List<String> requestContext = Arrays.asList(lineArray);

        //System.out.println(requestContext);

        //获取请求行
        String requestLine = lineArray[0];

        String[] requestLineArray = requestLine.split(" ");
        String requestURL = requestLineArray[1];

        //servlet动态资源处理方法
        //路径1:  /servlet/login
        //路径2:  /servlet/login?usn=123&&psw=110

        if(requestURL.indexOf("servlet")!=-1){

            //判断路径的种类
            String servletName = null;

            if(requestURL.indexOf("?")!=-1){//有？代表有参数
                servletName = requestURL.substring(requestURL.indexOf("/servlet/")+"/servlet/".length(),requestURL.indexOf("?"));
            }else{//无参数
                servletName = requestURL.substring(requestURL.indexOf("/servlet/")+"/servlet/".length());
            }

            servletName = servletName.trim();

            if(servletName.equals("")||servletName==null){


                transferResponse(outputStream,"HTTP/1.1",404,"Not Found","text/html","<h1>Not Found</h1>".getBytes());

                return;
            }

            //到这里以及获取到了 servletName 也就是 login

            //通过 servletName 去 Map中获取 对应的 servletClass类
            try {

                Servlet servlet = getServlet(servletName);//通过servletName 反射得到 servlet 并且存入map

                String content = servlet.doRequest(requestURL,s);// 通过 servlet的doRequest方法 获得content
                transferResponse(outputStream,"HTTP/1.1",200,"OK","text/html",content.getBytes());

            } catch (Exception e) {
                e.printStackTrace();
                transferResponse(outputStream,"HTTP/1.1",404,"Not Found","text/html","<h1>Not Found</h1>".getBytes());
                return;
            } finally {
                outputStream.flush();
                outputStream.close();
            }


        }else {

            //静态资源处理方法

            //如果请求的路径是其他静态路径则进行处理
            if(requestURL.equals("/favicon.ico")){
                transferResponse(outputStream,"HTTP/1.1",200,"OK","text/html","favicon.ico".getBytes());
                return;
            }

            String contentType = null;

            //判断文件的格式 -进行文件类型的转换
            if(requestURL.indexOf("htm")!=-1||requestURL.equals("/")){
                contentType = "text/html";
            }else if(requestURL.indexOf("jpg")!=-1||requestURL.indexOf("jpeg")!=-1){
                contentType = "image/jpeg";
            }else if(requestURL.indexOf("gif")!=-1){
                contentType = "image/gif";
            }else if(requestURL.indexOf("png")!=-1){
                contentType = "image/png";
            }else {
                contentType = "application/octet-stream";
            }


            //验证请求路径 如果是默认路径 也就是'/'则去index页面 否则 去请求的对应页面
            requestURL = requestURL.equals("/")? "index.html" :requestURL.substring(1);

            //去服务器内部的resource文件夹 找 对应文件名的文件
            URL resourcePath = getClass().getClassLoader().getResource(requestURL);

            //如果文件不存在 返回404
            if(resourcePath==null){

                transferResponse(outputStream,"HTTP/1.1",404,"Not Found","text/html","<h1>Not Found</h1>".getBytes());

                return;
            }

            //找得到就 去用bufferInputStream读出来
            byte[] responseDate = new byte[10000000];

            //然后把读到的内容放到responseDate中
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(resourcePath.getPath()));){
                while (bis.read(responseDate)!=-1);
                bis.read(responseDate);
            }

            transferResponse(outputStream,"HTTP/1.1",200,"OK",contentType,responseDate);

        }


    }

    public void transferResponse(OutputStream outputStream,String httpVersion,int code,String des,String contentType,byte[] responseDate) throws IOException {
        Response response = new Response();
        response.setStatusLine(new StatusLine("HTTP/1.1",200,"OK"));
        response.setHeaderLine(new HeaderLine("HttpServer/1.1",new Date().toString(),contentType));
        response.setEntityBody(responseDate);

        toResponse(outputStream,response);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer httpServer = new HttpServer(8383);
    }
}
