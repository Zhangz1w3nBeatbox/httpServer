package com.zzw.httpServer;

public class LoginServlet extends Servlet{
    @Override
    public String doRequest(String requestURL, String requestData) {
        return "登陆成功！";
    }
}
