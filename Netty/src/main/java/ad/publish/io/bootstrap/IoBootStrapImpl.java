package ad.publish.io.bootstrap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ad.publish.util.AppProperties;
import ad.publish.util.BeanFactory;

@Component
public class IoBootStrapImpl implements IoBootStrap{

    private static Logger log = LoggerFactory.getLogger(IoBootStrapImpl.class);

    private EventLoopGroup bossLoopGroup;
    private EventLoopGroup workLoogGroup;

    private Future<?> bossCloseFuture;
    private Future<?> workCloseFuture;

    @Override
    public void startup() throws Exception {

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        bossLoopGroup = new NioEventLoopGroup(2);
        workLoogGroup = new NioEventLoopGroup(Integer.parseInt(AppProperties.getValue("socket.work.thread.num")),
                new DefaultThreadFactory("work thread pool"), SelectorProvider.provider());

        serverBootstrap.group(bossLoopGroup , workLoogGroup);
        //serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        //serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);//关键是这句

        try {
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HttpResponseEncoder());
                    ch.pipeline().addLast(new HttpRequestDecoder());
                    ch.pipeline().addLast(new HttpObjectAggregator(1024));
                    ch.pipeline().addLast((ChannelHandler)BeanFactory.getBean("DrawImageHandler"));
                    ch.pipeline().addLast((ChannelHandler)BeanFactory.getBean("DistributionHandler"));
                }
            });

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

/*            Channel channel = serverBootstrap.bind(new InetSocketAddress(AppProperties.getValue("socket.address"),
                    Integer.parseInt(AppProperties.getValue("socket.port")))).sync().channel();
            channel.closeFuture().sync();*/


            serverBootstrap.bind(new InetSocketAddress(AppProperties.getValue("socket.address"),
                    Integer.parseInt(AppProperties.getValue("socket.port"))));
        } catch(Exception e) {
            log.error(e.getMessage());
        } finally {

        }

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
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
