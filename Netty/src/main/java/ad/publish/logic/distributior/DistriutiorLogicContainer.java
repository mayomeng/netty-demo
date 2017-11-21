package ad.publish.logic.distributior;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ad.publish.client.ClientPool;
import ad.publish.logic.LogicContainer;
import ad.publish.util.AppProperties;

import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;

@Component("DisruptorContainer")
public class DistriutiorLogicContainer implements LogicContainer {

    private ExecutorService threadPool;
    private WorkerPool<AdEvent> workerPool;

    @Autowired
    public AdRingBuffer adRingBuffer;

    @Autowired
    private ClientPool clientPool;

    @Override
    public void startup() throws Exception {

        int threadNum = Integer.parseInt(AppProperties.getValue("logic.thread.num"));
        threadPool = Executors.newFixedThreadPool(threadNum);

        SequenceBarrier consumerBarrier = adRingBuffer.getRingBuffer().newBarrier();

        AdEventHandler[] workHandlers = new AdEventHandler[threadNum];
        for (int i = 0 ; i < threadNum ; i++) {
            workHandlers[i] = new AdEventHandler(clientPool);
        }
        workerPool = new WorkerPool<>(adRingBuffer.getRingBuffer(),
                consumerBarrier, new IgnoreExceptionHandler(), workHandlers);
        Sequence[] sequences = workerPool.getWorkerSequences();
        adRingBuffer.getRingBuffer().addGatingSequences(sequences);
        workerPool.start(threadPool);

        clientPool.init();
    }

    @Override
    public void shutdown() throws Exception {
        workerPool.halt();
        threadPool.shutdown();
        clientPool.destory();
    }

}
