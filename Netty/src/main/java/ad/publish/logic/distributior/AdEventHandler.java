package ad.publish.logic.distributior;

import ad.publish.client.Client;
import ad.publish.client.ClientPool;
import ad.publish.client.audience.proto.parser.AudienceParserSample;
import ad.publish.task.Task;

import com.lmax.disruptor.WorkHandler;

public class AdEventHandler implements WorkHandler<AdEvent>{

    private ClientPool clientPool;

    public AdEventHandler(ClientPool clientPool) {
        this.clientPool = clientPool;
    }

    @Override
    public void onEvent(AdEvent param) throws Exception {
        Task task = param.getTask();
        Client client = clientPool.getClient(task, new AudienceParserSample());
        client.handle();
    }
}
