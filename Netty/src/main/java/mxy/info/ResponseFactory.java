package mxy.info;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.UnsupportedEncodingException;

public class ResponseFactory {
    public static Object getResponse(String result) throws UnsupportedEncodingException {


        String returnValue = result;
        String contentType="text/json";

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpHeaders httpHeaders = response.headers();
        httpHeaders.add("param", "value");
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, returnValue.length());
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS, "POST");
        response.headers().set(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS, "x-requested-with,content-type");

        ByteBuf responseContent = response.content();
        responseContent.writeBytes(returnValue.getBytes("UTF-8"));

        return response;
    }
}
