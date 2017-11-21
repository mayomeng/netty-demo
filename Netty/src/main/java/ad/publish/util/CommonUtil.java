package ad.publish.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import ad.publish.logic.CallBackHandler;

public class CommonUtil {

    private static HttpResponse getResponseForImage(File file) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("content-type", "image/png");
        response.headers().set("content-length", file.length());
        return response;
    }

    private static Object getResponse(String url) throws UnsupportedEncodingException {

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("Content-Type", "text/plain");

        ByteBuf responseContent = response.content();
        responseContent.writeBytes(url.getBytes("UTF-8"));

        return response;
    }

    public static void writeImg(ChannelHandlerContext context, File file, final CallBackHandler callBackHandler) throws FileNotFoundException {
        context.write(CommonUtil.getResponseForImage(file));

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, file.length());

        ChannelFuture future = context.writeAndFlush(region);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    callBackHandler.process();
                } else {
                    System.out.println("write failed.");
                }
                raf.close();
                context.channel().close();
            }
        });
    }

    public static void writeMessage(ChannelHandlerContext context, String url, final CallBackHandler callBackHandler) throws FileNotFoundException, UnsupportedEncodingException {

        ChannelFuture future = context.writeAndFlush(CommonUtil.getResponse(url));

        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    callBackHandler.process();
                } else {
                    System.out.println("write failed.");
                }
                context.channel().close();
            }
        });
    }
}
