package ad.publish.logic.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import ad.publish.logic.PublishProducerAdapter;
import ad.publish.task.Task;

@Component("ProducerQueue")
public class ProducerQueue extends PublishProducerAdapter {

    private BlockingQueue<Task> queue = new LinkedBlockingQueue<>();


    public void put(Task task) throws InterruptedException {
        queue.put(task);
    }

    public Task poll() {
        return queue.poll();
    }

    public Task take() throws InterruptedException {
        return queue.take();
    }
}