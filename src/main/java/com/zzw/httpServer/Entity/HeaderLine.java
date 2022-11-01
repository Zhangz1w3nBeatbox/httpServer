package com.zzw.httpServer.Entity;

public class HeaderLine {
    private String Server;
    private String Date;
    private String Content_Type;

    public HeaderLine(String server, String date) {
        Server = server;
        Date = date;
    }

    public HeaderLine(String server, String date, String content_Type) {
        Server = server;
        Date = date;
        Content_Type = content_Type;
    }

    public String getServer() {
        return Server;
    }

    public void setServer(String server) {
        Server = server;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getContent_Type() {
        return Content_Type;
    }

    public void setContent_Type(String content_Type) {
        Content_Type = content_Type;
    }

    @Override
    public String toString() {
        return
                "Server:" + Server + "\n\r" +
                 "Date:" + Date + "\n\r" +
                "Content-Type:" + Content_Type + "; charset=UTF-8"+"\n\r";
    }
}