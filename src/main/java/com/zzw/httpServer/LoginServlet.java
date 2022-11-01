package com.zzw.httpServer;

import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends Servlet{
    @Override
    public String doRequest(String requestURL, String requestData) {

        String[] requestArray = requestData.split("\r\n");
        String requestLine = requestArray[0];
            String[] requestLineArray = requestLine.split(" ");
            String method = requestLineArray[0];
            String URL = requestLineArray[1];
            String HttpVersion = requestLineArray[2];

        String paramStr = null;
        //get:   /servlet/login?username=123&password=123123
        if(method.equals("GET")){
             paramStr = URL.substring(URL.indexOf("?")+1);
        //post:  /servlet/login
        }else if(method.equals("POST")){

             paramStr = requestArray[requestArray.length-1];

             //处理一个空的请求体问题
             if(paramStr.equals("Accept-Language: zh-CN,zh-Hans;q=0.9")){
                 System.out.println("空请求体");
                 return "登陆失败!";
             }

        }

        boolean isAccess = validateAccount(paramStr);

        return isAccess?"登陆成功!":"登陆失败!";
    }


    private Map<String,String> toParamMap(String paramStr){
        Map<String,String> map = new HashMap<>();

        paramStr = paramStr.trim();

        if(paramStr.equals(" ")||paramStr==null) return map;

        //System.out.println(paramStr);

        String[] paramsArray = paramStr.split("&");

        for(String param:paramsArray){
            //System.out.println(param);
            String[] paramArr = param.split("=");
            if(paramArr.length==2){
                String key = paramArr[0];
                String val = paramArr[1];
                map.put(key,val);
            }
        }

        return map;
    }

    private boolean validateAccount(String paramStr){
        Map<String,String> paramMap = toParamMap(paramStr);

        String username = paramMap.get("username");
        String password = paramMap.get("password");
        if(!username.equals("zzw")||!password.equals("123123")){
            return false;
        }
        return true;
    }
}
