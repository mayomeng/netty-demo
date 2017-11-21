package ad.publish.logic;

import ad.publish.task.Task;

public class PublishProducerAdapter implements PublishProducer {

    @Override
    public void put(Task task) throws InterruptedException {

    }

    @Override
    public Task poll() {
        return null;
    }

    @Override
    public Task take() throws InterruptedException {
        return null;
    }

}
