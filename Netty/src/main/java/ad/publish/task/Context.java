package ad.publish.task;

import ad.publish.logic.CallBackHandler;


public interface Context {

    public void close();

    public void write(Object response, CallBackHandler ioFuture);

    public void writeAndFlush(Object response, CallBackHandler ioFuture) throws Exception;

    public void flush();
}
