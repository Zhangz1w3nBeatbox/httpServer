package com.zzw.httpServer.Entity;

import java.util.Arrays;
import java.util.Date;

public class Response {

   StatusLine statusLine;
   HeaderLine headerLine;
   byte[] entityBody;

   public Response(StatusLine statusLine, HeaderLine headerLine, byte[] entityBody) {
      this.statusLine = statusLine;
      this.headerLine = headerLine;
      this.entityBody = entityBody;
   }

   public Response() {
   }

   public StatusLine getStatusLine() {
      return statusLine;
   }

   public void setStatusLine(StatusLine statusLine) {
      this.statusLine = statusLine;
   }

   public HeaderLine getHeaderLine() {
      return headerLine;
   }

   public void setHeaderLine(HeaderLine headerLine) {
      this.headerLine = headerLine;
   }

   public byte[] getEntityBody() {
      return entityBody;
   }

   public void setEntityBody(byte[] entityBody) {
      this.entityBody = entityBody;
   }

   @Override
   public String toString() {
      return "Response{" +
              "statusLine=" + statusLine +
              ", headerLine=" + headerLine +
              ", entityBody=" + Arrays.toString(entityBody) +
              '}';
   }
}
