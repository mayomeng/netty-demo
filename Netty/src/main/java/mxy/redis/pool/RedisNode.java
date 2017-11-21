package mxy.redis.pool;

public class RedisNode {

    public String address;
    public long port;
    public long maxSlot;
    public long minSlot;

    public RedisNode(String address, long port, long maxSlot, long minSlot) {
        this.address = address;
        this.port = port;
        this.maxSlot = maxSlot;
        this.minSlot = minSlot;
    }

    public boolean isContainSlot(long slot) {
        if (slot >= minSlot && slot <= maxSlot) {
            return true;
        }
        return false;
    }
}
