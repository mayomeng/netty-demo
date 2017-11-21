package ad.publish.client.audience;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ad.publish.client.Client;
import ad.publish.client.ClientPool;
import ad.publish.client.audience.proto.parser.AudienceParser;
import ad.publish.client.register.RegisterCenter;
import ad.publish.task.Task;
import ad.publish.util.AppProperties;

@Component
public class AudienceClientPool implements ClientPool {

    private ChannelPool channelPool;

    private NioEventLoopGroup group;

    @Autowired
    private RegisterCenter registerCenter;

    @Override
    public void init() throws Exception {

        registerCenter.init();

        group = new NioEventLoopGroup(Integer.parseInt(AppProperties.getValue("audience.relation.client.thread.num")));
        Bootstrap b = new Bootstrap().channel(NioSocketChannel.class).group(group);
        b.remoteAddress(registerCenter.getAudienceAddress());

        channelPool = new FixedChannelPool(
                b, new AudiencePoolHandler(), Integer.parseInt(AppProperties.getValue("audience.relation.client.num")));
    }

    private Channel getChannel(Task task) throws Exception {
        Channel channel = null;

        try {
            channel = channelPool.acquire().get();
        } catch (InterruptedException | ExecutionException e) {
            reInit();
            channel = getChannel(task);
        }

        return channel;
    }

    @Override
    public Client getClient(Task task, AudienceParser protoParser) throws Exception {
        AudienceClient client = new AudienceClient(getChannel(task), task, this, protoParser);
        return client;
    }

    @Override
    public void release(Channel channel) {
        channelPool.release(channel);
    }

    public void reInit() throws Exception {
        destory();
        init();
    }

    @Override
    public void destory() throws Exception {
        channelPool.close();
        group.shutdownGracefully();
        registerCenter.close();
    }
}
