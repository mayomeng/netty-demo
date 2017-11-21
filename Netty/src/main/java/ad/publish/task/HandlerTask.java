package ad.publish.task;

import io.netty.channel.ChannelHandlerContext;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ad.publish.info.AdColumn;
import ad.publish.logic.CallBackHandler;
import ad.publish.util.CommonUtil;

@Component("HandlerTask")
public class HandlerTask extends TaskAdapter {

    private static Logger log = LoggerFactory.getLogger(HandlerTask.class);

    private AdColumn params;

    private InnerContext context;

    private static class InnerContext implements Context {

        private ChannelHandlerContext context;

        public void setContext(ChannelHandlerContext context) {
            this.context = context;
        }

        @Override
        public void close() {
            context.close();
        }

        @Override
        public void write(Object msg, final CallBackHandler ioFuture) {

        }

        @Override
        public void writeAndFlush(Object msg, final CallBackHandler callBackHandler) throws Exception {

            try {

                if (!context.executor().inEventLoop()) {
                    context.executor().execute(new Runnable() {
                        @Override
                        public void run() {
                            doWriteAndFlush(msg, callBackHandler);
                        }
                    });
                } else {
                    doWriteAndFlush(msg, callBackHandler);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        private void doWriteAndFlush(Object msg, final CallBackHandler callBackHandler) {
            try {
                if (msg instanceof File) {
                    CommonUtil.writeImg(context, (File)msg, callBackHandler);
                } else {
                    CommonUtil.writeMessage(context, (String)msg, callBackHandler);
                }
            } catch (Exception e) {
                context.channel().close();
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }

        }

        @Override
        public void flush() {
            context.flush();
        }

    }

    public HandlerTask() {
        context = new InnerContext();
    }

    public void setParams(AdColumn params) {
        this.params = params;
    }

    @Override
    public void setContext(Object context) {
        this.context.setContext((ChannelHandlerContext)context);
    }

    public Object getParams() {
        return params;
    }

    public Context getContext() {
        return context;
    }

}
