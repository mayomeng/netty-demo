package mxy.info;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public enum TaskQueue {

    instance;

    private BlockingQueue<HttpInfo> queue = new LinkedBlockingQueue<HttpInfo>();

    private TaskQueue() {}

    public void put(HttpInfo info) throws InterruptedException {
        queue.put(info);
    }

    public HttpInfo poll() {
        return queue.poll();
    }

    public HttpInfo take() throws InterruptedException {
        return queue.take();
    }

}
