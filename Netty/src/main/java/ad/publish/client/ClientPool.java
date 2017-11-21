package ad.publish.client;

import io.netty.channel.Channel;
import ad.publish.client.audience.proto.parser.AudienceParser;
import ad.publish.task.Task;

public interface ClientPool {

    public void init() throws Exception;

    public Client getClient(Task task, AudienceParser protoParser) throws Exception;

    public void release(Channel channel);

    public void destory() throws Exception;
}
