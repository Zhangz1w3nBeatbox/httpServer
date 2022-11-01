package com.zzw.httpServer.Entity;

public class StatusLine {
    private String HttpVersion;
    private int StatusCode;
    private String Description;

    public StatusLine(String httpVersion, int statusCode, String description) {
        HttpVersion = httpVersion;
        StatusCode = statusCode;
        Description = description;
    }

    public String getHttpVersion() {
        return HttpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        HttpVersion = httpVersion;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


    // String statusLine = ("HTTP/1.1 "+resCode+" "+resDes+"\r\n");
    @Override
    public String toString() {
        return
                HttpVersion +" "+
                 + StatusCode +" "+
                Description + "\r\n";

    }
}
