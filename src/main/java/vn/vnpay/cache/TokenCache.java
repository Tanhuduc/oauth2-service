package vn.vnpay.cache;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPooled;
import vn.vnpay.bean.LoginSession;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.config.pool.JedisPooledConfig;

/**
 * @Author: DucTN
 * Created: 18/08/2023
 **/
@Slf4j
public class TokenCache {
    private static final String LOGIN_SESSION_PREFIX = "LOGIN_SESSION:";
    private static final TokenCache INSTANCE = new TokenCache();

    private final Gson gson = GsonCommon.getInstance();

    private TokenCache() {
    }

    public static TokenCache getINSTANCE() {
        return INSTANCE;
    }

    private final JedisPooled jedisPooled = JedisPooledConfig.getInstance().getJedisPooled();

    public void saveLoginSession(Integer userId, String authorizationCode, LoginSession loginSession) {
        log.info("Save login session, userId: {}", userId);
        jedisPooled.hset(buildKey(LOGIN_SESSION_PREFIX, String.valueOf(userId)), authorizationCode, gson.toJson(loginSession));
    }

    public String getLoginSession(Integer userId, String authorizationCode) {
        log.info("Get login session, userId: {}", userId);
        return jedisPooled.hget(buildKey(LOGIN_SESSION_PREFIX, String.valueOf(userId)), authorizationCode);
    }

    public Long deleteLoginSession(Integer userId) {
        log.info("Delete login session, userId: {}", userId);
        return jedisPooled.del(buildKey(LOGIN_SESSION_PREFIX, String.valueOf(userId)));
    }

    private String buildKey(String prefix, String keyInfo) {
        return new StringBuilder(prefix).append(keyInfo).toString();
    }

}
