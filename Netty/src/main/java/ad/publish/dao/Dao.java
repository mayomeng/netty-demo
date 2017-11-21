package ad.publish.dao;

public interface Dao {

    public void init();

    public String get(String key);

    public void set(String key, String value);

    public byte[] getByte(String key);

    public void setByte(String key, byte[] value);

    public Boolean exists(final String key);

    public Long persist(final String key);

    public String type(final String key);

    public Long expire(final String key, final int seconds);

    public void close();
}
