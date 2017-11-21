package ad.publish.client;

import ad.publish.client.audience.proto.parser.AudienceParser;
import ad.publish.task.Task;

public interface Client {

    public void handle() throws Exception;

    public Task getTask();

    public ClientPool getPool();

    public AudienceParser getProtoParser();
}
