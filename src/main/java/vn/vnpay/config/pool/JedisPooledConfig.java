package vn.vnpay.config.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPooled;
import vn.vnpay.config.RedisConfig;

import java.time.Duration;
import java.util.Objects;

public class JedisPooledConfig {
    private static JedisPooledConfig instance;
    private final JedisPooled jedisPooled;
    private static final RedisConfig CONFIG = RedisConfig.getInstance();

    private JedisPooledConfig(JedisPooled jedisPooled) {
        this.jedisPooled = jedisPooled;
    }

    public JedisPooled getJedisPooled() {
        return this.jedisPooled;
    }

    public static JedisPooledConfig getInstance() {
        if (Objects.isNull(instance)) {
            final GenericObjectPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(CONFIG.getMaxTotal());
            config.setMaxIdle(CONFIG.getMaxIdle());
            config.setMinIdle(CONFIG.getMinIdle());
            config.setMinEvictableIdleTime(Duration.ofSeconds(CONFIG.getMinEvicIdleTimeSec()));
            config.setTimeBetweenEvictionRuns(Duration.ofSeconds(CONFIG.getTimeBetweenEvicRunsSec()));
            config.setBlockWhenExhausted(true);
            final JedisPooled jedisPooled = new JedisPooled(config, CONFIG.getHost(), CONFIG.getPort());
            instance = new JedisPooledConfig(jedisPooled);
        }

        return instance;
    }
}
