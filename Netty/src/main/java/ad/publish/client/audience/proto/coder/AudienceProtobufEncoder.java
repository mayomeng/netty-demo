package ad.publish.client.audience.proto.coder;

import static io.netty.buffer.Unpooled.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import com.google.protobuf.MessageLite;

@Sharable
public class AudienceProtobufEncoder extends MessageToMessageEncoder<MessageLite> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, List<Object> out) throws Exception {

        byte[] head = AudienceProtoUtil.getHeadByte(msg);
        byte[] body = msg.toByteArray();

        byte[] all = new byte[body.length + 1];
        System.arraycopy(head, 0, all, 0, head.length);
        System.arraycopy(body, 0, all, head.length, body.length);

        out.add(wrappedBuffer(all));
        return;
    }

}
