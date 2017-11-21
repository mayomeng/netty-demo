package ad.publish.client.register;

import java.net.SocketAddress;

public interface RegisterCenter {

    public void init() throws Exception;
    public SocketAddress getAudienceAddress() throws Exception;
    public void close() throws Exception;
}
