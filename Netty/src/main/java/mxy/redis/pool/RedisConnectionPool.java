package mxy.redis.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import ad.publish.util.AppProperties;

@Component("RedisConnectionPool")
public class RedisConnectionPool {

    // 连接保持时间
    private int keepAliveTime;

    private Map<String, InetSocketAddress> addressMap;
    private String[] addressArray;

    private AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    private Bootstrap bootstrap;

    private EventLoopGroup group;

    private List<RedisNode> redisNodeList;

    // 主线程与netty的工作线程间通信用map
    private static Map<Object, Object> communicationMap;

    private static Map<Object, Object> getCommunicationMap() {
        return communicationMap;
    }

    private static Object nullValue;

    public RedisConnectionPool() {
        System.out.println("");
    }

    public void startup() throws ExecutionException {

        intConfig();

        group = new NioEventLoopGroup();

        try {

            bootstrap = new Bootstrap();
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, keepAliveTime);
            bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            bootstrap.group(group).channel(NioSocketChannel.class);

            createPool();

            initRedisNodeList();

        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }

    public Channel getChannel(String address) throws InterruptedException, ExecutionException {
        InetSocketAddress inetSocketAddress = addressMap.get(address);
        SimpleChannelPool pool = poolMap.get(inetSocketAddress);
        Channel channel = pool.acquire().get();
        return channel;
    }

    public Object get(String key) throws Exception {
        Object value = null;

        InetSocketAddress address = getConnectionKey(key);

        SimpleChannelPool pool = poolMap.get(address);
        Channel channel = pool.acquire().get();
        value = RedisConnectionPool.getResponseFromRedis(channel, "get " + key);
        pool.release(channel);
        return value;
    }

    public void set(String key, Object value) throws Exception {

        InetSocketAddress address = getConnectionKey(key);

        SimpleChannelPool pool = poolMap.get(address);
        Channel channel = pool.acquire().get();
        value = RedisConnectionPool.getResponseFromRedis(channel, "set " + key + " " + value);
        pool.release(channel);

    }

    @Override
    protected void finalize() {
        destory();
    }

    public Future<?> destory() {

        Iterator<Entry<InetSocketAddress, SimpleChannelPool>> it = poolMap.iterator();
        while (it.hasNext()) {
            Entry<InetSocketAddress, SimpleChannelPool> entry = it.next();
            entry.getValue().close();
        }

        return group.shutdownGracefully();
    }

    private void intConfig() {
        keepAliveTime = Integer.parseInt(AppProperties.getValue("socket.keep.alive.time"));

        addressMap = new HashMap<>();
        addressArray = AppProperties.getValue("redis.cluster.address").split(",");
        for (int i = 0; i < addressArray.length; i++) {
            String[] address = addressArray[i].split(":");
            addressMap.put(addressArray[i], new InetSocketAddress(address[0], Integer.parseInt(address[1])));
        }

        communicationMap = new ConcurrentHashMap<>();
        nullValue = new Object();
    }

    private void createPool() {
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new SimpleChannelPool(bootstrap.remoteAddress(key), new RedisPoolHandler());
            }
        };
    }

    static class RedisPoolHandler extends AbstractChannelPoolHandler {

        @Override
        public void channelCreated(Channel ch) throws Exception {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new RedisDecoder());
            p.addLast(new RedisBulkStringAggregator());
            p.addLast(new RedisArrayAggregator());
            p.addLast(new RedisEncoder());
            p.addLast(new RedisClientReadHandler());
            p.addLast(new RedisClientWriteHandler());
        }

    }

    private void initRedisNodeList() throws NumberFormatException, InterruptedException, ExecutionException {

        Channel ch = getChannel(addressArray[0]);

        ArrayRedisMessage nodes = (ArrayRedisMessage)getResponseFromRedis(ch, "cluster slots");

        redisNodeList = new ArrayList<>();

        long minSlot;
        long maxSlot;
        String address;
        long port;
        for (RedisMessage tempNode : nodes.children()) {
            List<?> nodeInfoList = ((ArrayRedisMessage)tempNode).children();
            minSlot = (long)getValue(nodeInfoList.get(0));
            maxSlot = (long)getValue(nodeInfoList.get(1));

            List<?> addressInfoList = ((ArrayRedisMessage)nodeInfoList.get(2)).children();
            address = (String)getValue(addressInfoList.get(0));
            port = (long)getValue(addressInfoList.get(1));

            RedisNode redisNode = new RedisNode(address, port, maxSlot, minSlot);
            redisNodeList.add(redisNode);
        }

        ReferenceCountUtil.release(nodes);
    }

    private static Object getValue(Object msg) {

        if (msg instanceof SimpleStringRedisMessage) {
            return ((SimpleStringRedisMessage)msg).content();
        } else if (msg instanceof ErrorRedisMessage) {
            return ((ErrorRedisMessage)msg).content();
        } else if (msg instanceof IntegerRedisMessage) {
            return ((IntegerRedisMessage)msg).value();
        } else if (msg instanceof FullBulkStringRedisMessage) {
            return ((FullBulkStringRedisMessage)msg).content().toString(CharsetUtil.UTF_8);
        }

        return msg;
    }

    public InetSocketAddress getConnectionKey(String key) throws NumberFormatException, InterruptedException, ExecutionException {

        Channel ch = getChannel(addressArray[0]);

        long slot = (Long)getResponseFromRedis(ch, "cluster keyslot " + key);
        String connnectionKey = null;

        for (RedisNode node : redisNodeList) {
            if (node.isContainSlot(slot)) {
                connnectionKey = node.address + ":" + node.port;
                break;
            }
        }

        return addressMap.get(connnectionKey);
    }

    public static Object getResponseFromRedis(Channel ch, String cmd) {

        Object response;

        getCommunicationMap().put(ch, nullValue);
        ch.writeAndFlush(cmd);

        for (;;) {
            if (!getCommunicationMap().get(ch).equals(nullValue)) {
                response = getCommunicationMap().get(ch);
                getCommunicationMap().remove(ch);
                break;
            }
        }

        return getValue(response);
    }

    public static void saveResponse(Channel ch, Object msg) {
        getCommunicationMap().put(ch, msg);
    }
}
