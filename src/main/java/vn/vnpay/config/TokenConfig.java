package vn.vnpay.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.core.common.util.ReadFileYAMLUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Slf4j
@Getter
public class TokenConfig {
    private String secretKey;
    private String issuer;
    private long expireTime;
    private long refreshExpireTime;
    private Map<String, String> clients;

    private TokenConfig() {
    }

    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static TokenConfig instance;

    public static TokenConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = ReadFileYAMLUtil.read(PATH_FILE_CONFIG.getTokenFileConfigPath(), TokenConfig.class);
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static void refreshInstance() throws IOException {
        if (Objects.isNull(instance)) {
            instance = new TokenConfig();
        }
        TokenConfig tokenConfig = ReadFileYAMLUtil.read(PATH_FILE_CONFIG.getTokenFileConfigPath(), TokenConfig.class);
        instance.expireTime = tokenConfig.getExpireTime();
        instance.refreshExpireTime = tokenConfig.getRefreshExpireTime();
        instance.issuer = tokenConfig.getIssuer();
        instance.secretKey = tokenConfig.getSecretKey();
        instance.clients = tokenConfig.getClients();
    }
}
