package ad.publish.logic.distributior;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ad.publish.logic.PublishProducerAdapter;
import ad.publish.task.Task;

import com.lmax.disruptor.EventTranslatorOneArg;

@Component("AdEventProducer")
public class AdEventProducer extends PublishProducerAdapter{

    @Autowired
    public AdRingBuffer adRingBuffer;

    private static final EventTranslatorOneArg<AdEvent, Task> TRANSLATOR = new EventTranslatorOneArg<AdEvent, Task>() {
        public void translateTo(AdEvent event, long sequence, Task task) {
            event.setTask(task);
        }
    };

    @Override
    public void put(Task task) throws InterruptedException {
        adRingBuffer.getRingBuffer().publishEvent(TRANSLATOR, task);
    }
}
