package ad.publish.client.audience;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ad.publish.info.AdColumn;
import ad.publish.logic.CallBackHandlerForLogic;
import ad.publish.task.Task;

import com.google.protobuf.MessageLite;

@Sharable
public class AudienceRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        AudienceClient client = (AudienceClient)ctx.channel().attr(AudienceClient.AUDIENCE_CLIENT).get();
        Task task = client.getTask();
        AdColumn adColumn = (AdColumn)task.getParams();
        CallBackHandlerForLogic callBackHandler = new CallBackHandlerForLogic();
        callBackHandler.setParams(adColumn.getAdColumnId());
        task.getContext().writeAndFlush(client.getProtoParser().getObjectFromProto((MessageLite)msg), callBackHandler);

        client.getPool().release(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
