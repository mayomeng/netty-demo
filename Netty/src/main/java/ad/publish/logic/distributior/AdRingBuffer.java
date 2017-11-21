package ad.publish.logic.distributior;

import com.lmax.disruptor.RingBuffer;

public interface AdRingBuffer {
    public RingBuffer<AdEvent> getRingBuffer();
}
