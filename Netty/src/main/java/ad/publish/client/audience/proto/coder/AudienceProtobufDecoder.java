package ad.publish.client.audience.proto.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@Sharable
public class AudienceProtobufDecoder extends MessageToMessageDecoder<ByteBuf>{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        byte[] array;
        if (msg.hasArray()) {
            array = msg.array();
        } else {
            int length = msg.readableBytes();
            array = new byte[length];
            msg.getBytes(msg.readerIndex(), array, 0, length);
        }


;
        byte[] head = {array[0]};
        byte[] body = new byte[array.length - 1];
        System.arraycopy(array, 1, body, 0, array.length-1);

        out.add(AudienceProtoUtil.getProto(head, body));
    }

}
