package ad.publish.logic.distributior;

import org.springframework.stereotype.Component;

import ad.publish.util.AppProperties;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;

@Component
public class AdRingBufferImpl implements AdRingBuffer{

    private RingBuffer<AdEvent> ringBuffer;

    public AdRingBufferImpl() {
        AdEventFactory factory = new AdEventFactory();
        int ringBufferSize = Integer.parseInt(AppProperties.getValue("ring.buffer.size"));

        ringBuffer = RingBuffer.createMultiProducer(factory, ringBufferSize, new YieldingWaitStrategy ());
    }

    @Override
    public RingBuffer<AdEvent> getRingBuffer() {
        return ringBuffer;
    }
}
