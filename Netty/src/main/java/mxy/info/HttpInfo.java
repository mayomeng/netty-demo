package mxy.info;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;


public class HttpInfo implements BaseInfo {

    private String message;

    private Channel channel;

    private ChannelHandlerContext context;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }


}
