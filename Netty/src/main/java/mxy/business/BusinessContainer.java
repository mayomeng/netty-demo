package mxy.business;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mxy.info.HttpInfo;
import mxy.info.TaskQueue;

public class BusinessContainer implements Runnable{

    public void execut() throws InterruptedException, ExecutionException, UnsupportedEncodingException {

        System.out.println("BusinessContainer is running.");

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        try {
            for(;;) {
                final HttpInfo info = TaskQueue.instance.poll();
                if (info == null) {
                    continue;
                }

/*                final ChannelHandlerContext ctx = info.getContext();
                if (!ctx.executor().inEventLoop()) {
                    ctx.executor().execute(new Runnable () {
                        public void run() {
                            try {
                                ctx.writeAndFlush(ResponseFactory.getResponse("result : " + info.getMessage()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }*/
                BusinessTask task = new BusinessTask(info);
                threadPool.submit(task);

            }
        } finally {
            //threadPool.shutdown();
        }
    }

    public void run() {
        try {
            execut();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
