package ad.publish.client.audience.proto.coder;

import ad.publish.client.audience.proto.AudienceProtoBuilder.RequestProto;
import ad.publish.client.audience.proto.AudienceProtoBuilder.ResponseProto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

public class AudienceProtoUtil {

    private static byte REQUEST = 0x00;
    private static byte RESPONSE = 0x01;

    public static byte[] getHeadByte(MessageLite protoType) {

        byte[] head = {0x0f};

        if (protoType instanceof RequestProto) {
            head[0] = REQUEST;
        } else if (protoType instanceof ResponseProto) {
            head[0] = RESPONSE;
        }
        return head;
    }

    public static MessageLite getProto(byte[] head, byte[] body) throws InvalidProtocolBufferException {

        MessageLite proto = null;

        if (head[0] == REQUEST) {
            proto = RequestProto.parseFrom(body);
        } else if (head[0] == RESPONSE) {
            proto = ResponseProto.parseFrom(body);
        }

        return proto;
    }

}
