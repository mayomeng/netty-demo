package ad.publish.logic;

import ad.publish.task.Task;


public interface PublishProducer {

    public void put(Task task) throws InterruptedException;

    public Task poll();

    public Task take() throws InterruptedException;
}
