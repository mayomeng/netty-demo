package ad.publish.client.audience.proto.parser;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import ad.publish.client.audience.proto.AudienceProtoBuilder.RequestProto;
import ad.publish.client.audience.proto.AudienceProtoBuilder.ResponseProto;
import ad.publish.info.AdColumn;

import com.google.protobuf.MessageLite;

public class AudienceParserSample implements AudienceParser {

    @Override
    public MessageLite getProtoFromObject(Object param) {
        AdColumn adParam = (AdColumn)param;

        RequestProto.Builder resquestBuilder = RequestProto.newBuilder();
        resquestBuilder.setId(Integer.parseInt(adParam.getAdColumnId()));
        resquestBuilder.setType(adParam.getType());

        return resquestBuilder.build();
    }

    @Override
    public Object getObjectFromProto(Object param) {
        ResponseProto response = (ResponseProto)param;

        Map<String,String> map = new HashMap<String,String>();
        map.put("filePath", response.getFilePath());
        JSONObject resultJSON = JSONObject.fromObject(map);

        return "getFilePath"+"("+resultJSON.toString()+")";
    }



}
