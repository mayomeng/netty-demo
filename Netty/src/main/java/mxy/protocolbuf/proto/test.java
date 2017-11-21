package mxy.protocolbuf.proto;

import mxy.protocolbuf.proto.AdBuilder.AdEntity;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class test {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        AdBuilder.AdEntity.Builder adBuilder = AdBuilder.AdEntity.newBuilder();

        adBuilder.setId(2);
        adBuilder.setName("a");
        adBuilder.setEmail("test@tet.com");

        AdEntity entity = adBuilder.build();
        System.out.println(entity);
        byte[] byteArray =entity.toByteArray();

/*        AdEntity entity2 = AdEntity.parseFrom(byteArray);
        System.out.println(entity2);*/


        DynamicMessage mes = DynamicMessage.parseFrom(AdEntity.getDescriptor(), byteArray);
        String email = (String)mes.getField(AdEntity.getDescriptor().findFieldByName("email"));
        DynamicMessage.Builder builder = mes.newBuilder(AdEntity.getDescriptor());
        builder.build();
        System.out.println(AdEntity.getDescriptor().getName());
    }

}
