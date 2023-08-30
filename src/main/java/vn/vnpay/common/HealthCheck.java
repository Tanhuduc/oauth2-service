package vn.vnpay.common;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPooled;
import vn.vnpay.config.pool.DBCPDataSource;
import vn.vnpay.config.pool.JedisPooledConfig;

import java.sql.Connection;
import java.util.Objects;

/**
 * @author: DucTN
 * Created: 02/08/2023
 **/
@Slf4j
public class HealthCheck {
    private HealthCheck() {
    }

    public static boolean health() {
        return healthCheckDB() && healthCheckRedis();
    }
    public static boolean healthCheckDB() {
        Connection connection;
        try {
            connection = DBCPDataSource.getConnection();
            if (Objects.isNull(connection)) {
                log.info("[healthCheckDB] DB connection is failed!");
                return false;
            }
            log.info("[healthCheckDB] DB connection is healthy!");
            connection.close();
            return true;
        } catch (Exception e) {
            log.info("[healthCheckDB] Has error");
            log.error("[healthCheckDB] Error when checking DB connection, exception: ", e);
            return false;
        }
    }

    private static boolean healthCheckRedis() {
        JedisPooled jedisPooled = JedisPooledConfig.getInstance().getJedisPooled();
        try {
            boolean result = jedisPooled.getPool().getResource().ping();
            if(!result) {
                log.info("[healthCheckRedis] Redis connection is not healthy");
                return false;
            }
            log.info("[healthCheckRedis] Redis connection is healthy!");
            return true;
        } catch (Exception e) {
            log.info("[healthCheckRedis] Has error");
            log.error("[healthCheckRedis] Error when checking redis connection, exception: ", e);
            return false;
        }
    }
}
