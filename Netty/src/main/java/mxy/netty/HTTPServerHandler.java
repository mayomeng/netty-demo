package mxy.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author yinwenjie
 */
@Sharable
class HTTPServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 日志
     */
    private static Log LOGGER = LogFactory.getLog(HTTPServerHandler.class);

    /**
     * 由于一次httpcontent可能没有传输完全部的请求信息。所以这里要做一个连续的记录
     * 然后在channelReadComplete方法中（执行了这个方法说明这次所有的http内容都传输完了）进行处理
     */
    private static AttributeKey<StringBuffer> CONNTENT = AttributeKey.valueOf("content");

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        /*
         * 在测试中，我们首先取出客户端传来的参数、URL信息，并且返回给一个确认信息。
         * 要使用HTTP服务，我们首先要了解Netty中http的格式，如下：
         * ----------------------------------------------
         * | http request | http content | http content |
         * ----------------------------------------------
         *
         * 所以通过HttpRequestDecoder channel handler解码后的msg可能是两种类型：
         * HttpRquest：里面包含了请求head、请求的url等信息
         * HttpContent：请求的主体内容
         * */

        Thread thread = Thread.currentThread();
        System.out.println(thread.getName());

        if(msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest)msg;
            HttpMethod method = request.getMethod();

            String methodName = method.name();
            String url = request.getUri();
            HTTPServerHandler.LOGGER.info("methodName = " + methodName + " && url = " + url);
        }

        //如果条件成立，则在这个代码段实现http请求内容的累加
        if(msg instanceof HttpContent) {
            StringBuffer content = ctx.attr(HTTPServerHandler.CONNTENT).get();
            if(content == null) {
                content = new StringBuffer();
                ctx.attr(HTTPServerHandler.CONNTENT).set(content);
            }

            HttpContent httpContent = (HttpContent)msg;
            ByteBuf contentBuf = httpContent.content();
            String preContent = contentBuf.toString(io.netty.util.CharsetUtil.UTF_8);
            content.append(preContent);
        }
    }

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        HTTPServerHandler.LOGGER.info("super.channelReadComplete(ChannelHandlerContext ctx)");

        /*
         * 一旦本次http请求传输完成，则可以进行业务处理了。
         * 并且返回响应
         * */

        StringBuffer content = ctx.attr(HTTPServerHandler.CONNTENT).get();
        HTTPServerHandler.LOGGER.info("http客户端传来的信息为：" + content);



        //开始返回信息了
        String returnValue = "return response";
        String contentType="text/json";

/*        if (true) {
            returnValue = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.0 Transitional//EN'><html><head><title>Untitled</title></head><body>Hello World!</body></html>";
            contentType="text/html";
        }*/
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpHeaders httpHeaders = response.headers();
        //这些就是http response 的head信息咯，参见http规范。另外您还可以设置自己的head属性
        httpHeaders.add("param", "value");
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
        //一定要设置长度，否则http客户端会一直等待（因为返回的信息长度客户端不知道）
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, returnValue.length());

        ByteBuf responseContent = response.content();
        responseContent.writeBytes(returnValue.getBytes("UTF-8"));

        //开始返回
        ctx.writeAndFlush(response);
    }
}