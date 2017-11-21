package mxy.netty;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import mxy.info.HttpInfo;
import mxy.info.ResponseFactory;

@Sharable
public class ResutlHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if (msg instanceof FullHttpResponse) {
            ctx.writeAndFlush(msg);
        }

        System.out.println(Thread.currentThread().getName());
        HttpInfo info = new HttpInfo();
        info.setMessage((String)msg);
        info.setContext(ctx);
        info.setChannel(ctx.channel());
        //ctx.channel().close();
        if ("test".equals(msg)) {
            System.out.println(Thread.currentThread().getName() + " : the business .");
            // TaskQueue.instance.put(info);
            ctx.channel().writeAndFlush(msg);
        } else {
            System.out.println(Thread.currentThread().getName() + " : the return .");
            ctx.writeAndFlush(ResponseFactory.getResponse((String)msg));
        }
    }
}
