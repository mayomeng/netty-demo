package ad.publish.main;

import java.io.IOException;

import ad.publish.util.AppProperties;
import ad.publish.util.BeanFactory;

public class Main {

    public static void main(String[] args) throws IOException {

        AppProperties.init();
        BeanFactory.init();

        PublishServer publishServer = (PublishServer)BeanFactory.getBean("PublishServer");
        try {

            new Thread(publishServer.init()).start();;
            publishServer.startup();

        } catch (Exception e) {

        }

    }

}
