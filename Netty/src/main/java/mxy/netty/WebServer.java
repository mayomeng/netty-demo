package mxy.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;

import mxy.business.BusinessContainer;


public class WebServer {

    public static void main(String[] args) throws Exception {

        BusinessContainer businessContainer = new BusinessContainer();
        new Thread(businessContainer).start();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        ThreadFactory threadFactory = new DefaultThreadFactory("work thread pool");
        //int processorsNumber = Runtime.getRuntime().availableProcessors();
        EventLoopGroup workLoogGroup = new NioEventLoopGroup(1, threadFactory, SelectorProvider.provider());
        serverBootstrap.group(bossLoopGroup , workLoogGroup);

        try {
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new HttpResponseEncoder());
                    ch.pipeline().addLast(new ResutlHandler());

                    ch.pipeline().addLast(new HttpRequestDecoder());
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(2000, 0, 2000));
                    ch.pipeline().addLast(new BusinessHandler());

                }
            });

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);


/*            Channel channel = serverBootstrap.bind(new InetSocketAddress("192.168.196.181",
                    985)).sync().channel();
            channel.closeFuture().sync();*/

            serverBootstrap.bind(new InetSocketAddress("192.168.196.181",
                    985));
            serverBootstrap.bind(new InetSocketAddress("192.168.196.181",
                    986));

        } finally {
/*            bossLoopGroup.shutdownGracefully();
            workLoogGroup.shutdownGracefully();*/
        }


    }
}