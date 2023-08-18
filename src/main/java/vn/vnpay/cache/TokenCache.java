package vn.vnpay.cache;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPooled;
import vn.vnpay.config.pool.JedisPooledConfig;

/**
 * @Author: DucTN
 * Created: 18/08/2023
 **/
@Slf4j
public class TokenCache {
    private static final String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN:";
    private static final TokenCache INSTANCE = new TokenCache();

    private TokenCache() {
    }

    public static TokenCache getINSTANCE() {
        return INSTANCE;
    }

    private final JedisPooled jedisPooled = JedisPooledConfig.getInstance().getJedisPooled();

    public void saveRefreshToken(String clientId, String userId, String refreshToken) {
        log.info("Save refresh token, clientId: {}, userId: {}", clientId, userId);
        jedisPooled.hset(buildKey(clientId), userId, refreshToken);
    }

    public String getRefreshToken(String clientId, String userId) {
        log.info("Get refresh token, clientId: {}, userId: {}", clientId, userId);
        return jedisPooled.hget(buildKey(clientId), userId);
    }

    public Long deleteRefreshToken(String clientId, String userId) {
        log.info("Delete refresh token, clientId: {}, userId: {}", clientId, userId);
        return jedisPooled.hdel(buildKey(clientId), userId);
    }

    private String buildKey(String clientId) {
        return new StringBuilder(REFRESH_TOKEN_PREFIX).append(clientId).toString();
    }

}
