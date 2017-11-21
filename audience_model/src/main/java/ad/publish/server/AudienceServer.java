package ad.publish.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;

import ad.publish.io.handler.AudnenceResponseHandler;
import ad.publish.proto.coder.AudienceProtobufDecoder;
import ad.publish.proto.coder.AudienceProtobufEncoder;
import ad.publish.util.AppProperties;

public class AudienceServer {

    private EventLoopGroup bossLoopGroup;
    private EventLoopGroup workLoogGroup;

    private Future<?> bossCloseFuture;
    private Future<?> workCloseFuture;

    public void startup() throws Exception {

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        bossLoopGroup = new NioEventLoopGroup(2);
        workLoogGroup = new NioEventLoopGroup(Integer.parseInt(AppProperties.getValue("socket.work.thread.num")),
                new DefaultThreadFactory("work thread pool"), SelectorProvider.provider());

        serverBootstrap.group(bossLoopGroup, workLoogGroup);

        try {
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    ch.pipeline().addLast(new AudienceProtobufEncoder());

                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    ch.pipeline().addLast(new AudienceProtobufDecoder());
                    ch.pipeline().addLast(new AudnenceResponseHandler());
                }
            });

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            serverBootstrap.bind(new InetSocketAddress(AppProperties.getValue("socket.address"),
                    Integer.parseInt(AppProperties.getValue("socket.port"))));
        } catch (Exception e) {

        } finally {

        }

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void shutdown() throws InterruptedException {
        bossCloseFuture = bossLoopGroup.shutdownGracefully();
        workCloseFuture = workLoogGroup.shutdownGracefully();

        bossCloseFuture.addListeners(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isDone()) {
                    System.out.println("boss is over.");
                }
            }
        });

        workCloseFuture.addListeners(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                if (future.isDone()) {
                    System.out.println("work is over.");
                }
            }
        });
    }

}
