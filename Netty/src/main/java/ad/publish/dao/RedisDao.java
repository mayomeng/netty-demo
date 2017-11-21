package ad.publish.dao;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClusterCommand;
import redis.clients.jedis.JedisClusterConnectionHandler;
import redis.clients.jedis.JedisSlotBasedConnectionHandler;

@Repository
public class RedisDao implements Dao {

    private static final int DEFAULT_TIMEOUT = 2000;
    private static final int DEFAULT_MAX_REDIRECTIONS = 20;

    private JedisClusterConnectionHandler connectionHandler;

    public RedisDao() {
    }

    @Override
    public void init() {
        Set<HostAndPort> addressSet = new HashSet<HostAndPort>();
        addressSet.add(new HostAndPort("192.168.133.128", 7001));
        addressSet.add(new HostAndPort("192.168.133.128", 7002));
        addressSet.add(new HostAndPort("192.168.133.128", 7003));
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(1);
        poolConfig.setMaxIdle(1);
        poolConfig.setMaxWaitMillis(1000);
        connectionHandler = new JedisSlotBasedConnectionHandler(addressSet, poolConfig, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
    }

    @Override
    public String get(String key) {
        return new JedisClusterCommand<String>(connectionHandler, DEFAULT_MAX_REDIRECTIONS) {
            @Override
            public String execute(Jedis connection) {
                return connection.get(key);
            }
        }.run(key);
    }

    @Override
    public void set(String key, String value) {
        new JedisClusterCommand<String>(connectionHandler, DEFAULT_MAX_REDIRECTIONS) {
            @Override
            public String execute(Jedis connection) {
                return connection.set(key, value);
            }
        }.run(key);
    }

    @Override
    public byte[] getByte(String key) {
        return new JedisClusterCommand<byte[]>(connectionHandler, DEFAULT_MAX_REDIRECTIONS) {
            @Override
            public byte[] execute(Jedis connection) {
                return connection.get(key.getBytes());
            }
        }.run(key);
    }

    @Override
    public void setByte(String key, byte[] value) {
        new JedisClusterCommand<String>(connectionHandler, DEFAULT_MAX_REDIRECTIONS) {
            @Override
            public String execute(Jedis connection) {
                return connection.set(key.getBytes(), value);
            }
        }.run(key);
    }

    @Override
    public Boolean exists(String key) {
        return new JedisClusterCommand<Boolean>(connectionHandler, DEFAULT_MAX_REDIRECTIONS) {
            @Override
            public Boolean execute(Jedis connection) {
                return connection.exists(key);
            }
        }.run(key);
    }

    @Override
    public Long persist(String key) {
        return new JedisClusterCommand<Long>(connectionHandler, DEFAULT_MAX_REDIRECTIONS) {
            @Override
            public Long execute(Jedis connection) {
                return connection.persist(key);
            }
        }.run(key);
    }

    @Override
    public String type(String key) {
        return new JedisClusterCommand<String>(connectionHandler, DEFAULT_MAX_REDIRECTIONS) {
            @Override
            public String execute(Jedis connection) {
                return connection.type(key);
            }
        }.run(key);
    }

    @Override
    public Long expire(String key, int seconds) {
        return new JedisClusterCommand<Long>(connectionHandler, DEFAULT_MAX_REDIRECTIONS) {
            @Override
            public Long execute(Jedis connection) {
                return connection.expire(key, seconds);
            }
        }.run(key);
    }

    @Override
    public void close() {
        connectionHandler.close();
    }
}
