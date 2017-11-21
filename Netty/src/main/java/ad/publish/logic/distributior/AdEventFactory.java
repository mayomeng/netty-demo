package ad.publish.logic.distributior;

import com.lmax.disruptor.EventFactory;

public class AdEventFactory implements EventFactory<AdEvent>{

    public AdEvent newInstance() {
        return new AdEvent();
    }

}
