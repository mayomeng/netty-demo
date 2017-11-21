package ad.publish.client.audience.proto.parser;

import com.google.protobuf.MessageLite;

public interface AudienceParser {

    public MessageLite getProtoFromObject(Object param);

    public Object getObjectFromProto(Object param);
}
