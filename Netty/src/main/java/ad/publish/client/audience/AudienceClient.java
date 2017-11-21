package ad.publish.client.audience;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import ad.publish.client.Client;
import ad.publish.client.ClientPool;
import ad.publish.client.audience.proto.parser.AudienceParser;
import ad.publish.task.Task;

import com.google.protobuf.MessageLite;

public class AudienceClient implements Client {

    public final static AttributeKey<AudienceClient> AUDIENCE_CLIENT = AttributeKey.newInstance("AudienceClient");

    private Channel channel;

    private Task task;

    private AudienceClientPool pool;

    private AudienceParser protoParser;

    public AudienceClient(Channel channel, Task task, AudienceClientPool pool, AudienceParser protoParser) {
        this.channel = channel;
        this.task = task;
        this.pool = pool;
        this.protoParser = protoParser;

        this.channel.attr(AudienceClient.AUDIENCE_CLIENT).set(this);
    }

    @Override
    public void handle() throws Exception {

        try {
            writeAndFlush(protoParser.getProtoFromObject(task.getParams()));
        } catch (Exception e) {
            pool.reInit();
            writeAndFlush(protoParser.getProtoFromObject(task.getParams()));
        }
    }

    private void writeAndFlush(MessageLite reauest) {
        ChannelFuture future = channel.writeAndFlush(reauest);
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {

            }
        });
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public ClientPool getPool() {
        return pool;
    }

    @Override
    public AudienceParser getProtoParser() {
        return protoParser;
    }

}
