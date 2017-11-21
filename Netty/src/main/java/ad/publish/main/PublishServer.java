package ad.publish.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ad.publish.dao.Dao;
import ad.publish.io.bootstrap.IoBootStrap;
import ad.publish.logic.LogicContainer;

@Component("PublishServer")
public class PublishServer {

    private static Logger log = LoggerFactory.getLogger(PublishServer.class);

    @Autowired
    private IoBootStrap ioBootStrap;

    @Autowired
    @Qualifier("DisruptorContainer")
    private LogicContainer logicContainer;

    @Autowired
    private Dao dao;

    public Runnable init() {
        ClostThread clostThread = new ClostThread(this);
        return clostThread;
    }

    public void startup() throws Exception {
        dao.init();
        logicContainer.startup();
        ioBootStrap.startup();
    }

    public void shutdown() throws Exception {
        logicContainer.shutdown();
        ioBootStrap.shutdown();
        dao.close();
    }

    private static class ClostThread implements Runnable {

        private PublishServer server;

        public ClostThread(PublishServer server) {
            this.server = server;
        }

        @Override
        public void run() {
            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                final String input = in.readLine();
                final String line = input != null ? input.trim() : null;
                if ("quit".equalsIgnoreCase(line)) {
                    server.shutdown();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

    }
}
