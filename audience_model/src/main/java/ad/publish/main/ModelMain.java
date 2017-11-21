package ad.publish.main;

import ad.publish.server.AudienceServer;
import ad.publish.zookeeper.RegisterCenter;

public class ModelMain {

    public static void main(String[] args) throws Exception {
        AudienceServer server = new AudienceServer();
        server.startup();

        RegisterCenter registerCenter = new RegisterCenter();
        registerCenter.regist();
    }

}
