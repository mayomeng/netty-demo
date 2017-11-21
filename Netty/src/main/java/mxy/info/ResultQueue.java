package mxy.info;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public enum ResultQueue {

    instance;

    private BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>();

    private ResultQueue() {}

    public void put(Object result) throws InterruptedException {
        queue.put(result);
    }

    public Object poll() {
        return queue.poll();
    }

    public Object take() throws InterruptedException {
        return queue.take();
    }

}
