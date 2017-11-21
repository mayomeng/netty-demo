package mxy.netty;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author yinwenjie
 */
@Sharable
class ImageServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject param) throws Exception {

        HttpRequest request = (HttpRequest)param;

        if ("/favicon.ico".equals(request.uri()) || !request.uri().contains("/getAdImg")) {
            return;
        }


        File file = new File("C:\\Users\\meng_xiangyu\\Desktop\\img\\1.png");

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("content-type", "image/png");
        response.headers().set("content-length", file.length());

        if ("/getAdImg/download".equals(request.uri())) {
            response.headers().set("Content-Disposition", "attachment; filename=test.png");
        }

/*        ChannelFuture resopnseHeaderFuture = ctx.write(response);
        resopnseHeaderFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture paramF) throws Exception {
                System.out.println("head over.");
            }
        });*/
        ctx.channel().write(response);

        //Thread.sleep(5000);

        final RandomAccessFile raf = new RandomAccessFile(file, "r");
        final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, file.length());

/*        ChannelFuture responseContentFuture = ctx.writeAndFlush(region);
        responseContentFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture paramF) throws Exception {
                raf.close();
                System.out.println("content over.");
            }
        });*/
        ctx.channel().writeAndFlush(region);


    }

}