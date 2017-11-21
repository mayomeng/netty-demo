package mxy.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

@Sharable
class BusinessHandler extends ChannelInboundHandlerAdapter {

    private StringBuilder sb = new StringBuilder();

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

        if(msg instanceof HttpRequest) {
            // System.out.println(Thread.currentThread().getName() + " : the request head");
        }

        if(msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent)msg;
            ByteBuf contentBuf = httpContent.content();
            String content = contentBuf.toString(io.netty.util.CharsetUtil.UTF_8);
            sb.append(content);
            //System.out.println(Thread.currentThread().getName() + " : the request content : " + content.length());
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       // ctx.writeAndFlush(sb.toString());
        // System.out.println(Thread.currentThread().getName() + " : " + sb.toString());
        ctx.write(sb.toString());
    }
}