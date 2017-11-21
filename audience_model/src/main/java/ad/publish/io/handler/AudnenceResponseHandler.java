package ad.publish.io.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ad.publish.proto.AudienceProtoBuilder;
import ad.publish.proto.AudienceProtoBuilder.RequestProto;

@Sharable
public class AudnenceResponseHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

        Object result = (RequestProto)msg;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        AudienceProtoBuilder.ResponseProto.Builder builder = AudienceProtoBuilder.ResponseProto.newBuilder();
        builder.setFilePath("C:\\Users\\meng_xiangyu\\Desktop\\img\\audience.png");
        ctx.writeAndFlush(builder.build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
