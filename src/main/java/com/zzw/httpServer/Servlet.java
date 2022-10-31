package com.zzw.httpServer;

//处理动态请求逻辑
public abstract class Servlet {
    public abstract String doRequest(String requestURL,String requestData);
}
