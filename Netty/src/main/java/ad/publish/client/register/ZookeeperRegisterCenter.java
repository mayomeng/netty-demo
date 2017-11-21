package ad.publish.client.register;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import ad.publish.util.AppProperties;

@Component
public class ZookeeperRegisterCenter implements RegisterCenter{

    private Stat stat = new Stat();
    private ZooKeeper zk;

    @Override
    public void init() throws Exception {
        zk = new ZooKeeper(AppProperties.getValue("zookeeper.connect.string"), Integer.parseInt(AppProperties.getValue("zookeeper.time.out")), new Watcher() {
            public void process(WatchedEvent event) {
                //
            }
        });
    }

    @Override
    public SocketAddress getAudienceAddress() throws Exception {

        Map<String, String> audienceModelAddressMap = new HashMap<>();
        Map<String, Integer> audienceRelationCountMap = new HashMap<>();
        List<String> audienceModelList = zk.getChildren(AppProperties.getValue("audience.server.parent.name"), true);
        for (String audienceModel : audienceModelList) {
            byte[] audienceModelAddress = zk.getData(AppProperties.getValue("audience.server.parent.name") + "/" + audienceModel, false, stat);
            audienceModelAddressMap.put(audienceModel, new String(audienceModelAddress, "utf-8"));
            audienceRelationCountMap.put(audienceModel, 0);
        }

        List<String> audienceRelationList = zk.getChildren(AppProperties.getValue("audience.relation.name"), true);
        for (String audienceRelation : audienceRelationList) {
            String audienceModel = audienceRelation.split(":")[0];
            if (audienceModelAddressMap.containsKey(audienceModel)) {
                int relationNum = audienceRelationCountMap.get(audienceModel) + 1;
                audienceRelationCountMap.put(audienceModel, relationNum);
            }
        }

        List<Map.Entry<String, Integer>> relationCountList = new ArrayList<Map.Entry<String, Integer>>(audienceRelationCountMap.entrySet());
        Collections.sort(relationCountList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        String address = audienceModelAddressMap.get(relationCountList.get(0).getKey());
        String host = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);

        regist(relationCountList.get(0).getKey());

        return InetSocketAddress.createUnresolved(host, port);
    }

    private void regist(String audienceModelName) throws Exception {

        Stat stat = zk.exists(AppProperties.getValue("audience.relation.name"), true);
        if (stat == null) {
            zk.create(AppProperties.getValue("audience.relation.name"), null,Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        String relation = audienceModelName + ":" + AppProperties.getValue("server.name");
        zk.create(AppProperties.getValue("audience.relation.name") + "/" + relation, null,Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    @Override
    public void close() throws Exception {
        zk.close();
    }
}
