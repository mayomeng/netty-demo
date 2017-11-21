package mxy.redis;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class JedisTemplate {

    public static void main(String[] args) {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1);
        poolConfig.setMaxIdle(1);
        poolConfig.setMaxWaitMillis(1000);
        Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
        nodes.add(new HostAndPort("192.168.133.128", 7000));
        nodes.add(new HostAndPort("192.168.133.128", 7001));
        nodes.add(new HostAndPort("192.168.133.128", 7002));

        JedisCluster cluster = new JedisCluster(nodes, poolConfig);

        cluster.set("age", "18");
        System.out.println(cluster.get("age"));
        cluster.del("age");
        System.out.println(cluster.get("age"));

        try {
            cluster.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
