package mxy.business;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import mxy.info.HttpInfo;
import mxy.info.ResponseFactory;

public class BusinessTask implements Runnable {

    private HttpInfo info;

    public BusinessTask(HttpInfo info) {
        this.info = info;
    }

    public void run() {

        try {
            System.out.println(Thread.currentThread().getName() + " : the task run");
            String result = "the rquest of " + info.getMessage() + " from business.";
            Thread.sleep(8000);
            ChannelFuture channelFuture = info.getContext().writeAndFlush(ResponseFactory.getResponse(result));

            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture paramF) throws Exception {
                    System.out.println(Thread.currentThread().getName() + " : the task complete");
                    info.getContext().close();
                }
            });

            // info.getChannel().writeAndFlush(ResponseFactory.getResponse(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
