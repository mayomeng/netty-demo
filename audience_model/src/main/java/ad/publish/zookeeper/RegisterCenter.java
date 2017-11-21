package ad.publish.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import ad.publish.util.AppProperties;

public class RegisterCenter {

    public void regist() throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk = new ZooKeeper(AppProperties.getValue("zookeeper.connect.string"), Integer.parseInt(AppProperties.getValue("zookeeper.time.out")), new Watcher() {
            public void process(WatchedEvent event) {
                //
            }
        });

        Stat stat = zk.exists(AppProperties.getValue("audience.server.parent.name"), true);
        if (stat == null) {
            zk.create(AppProperties.getValue("audience.server.parent.name"), null,Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        String value = AppProperties.getValue("socket.address") + ":" + AppProperties.getValue("socket.port");
        zk.create(AppProperties.getValue("audience.server.model.name"), value.getBytes("utf-8"),Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }
}
