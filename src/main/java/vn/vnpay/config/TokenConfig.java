package vn.vnpay.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.common.util.ReadFileYAMLUtil;

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
    private Map<String, TokenClientConfig> clients;

    private TokenConfig() {
    }

    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static TokenConfig instance;

    public static TokenConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = initInstance(PATH_FILE_CONFIG.getTokenFileConfigPath());
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
        instance.clients = tokenConfig.getClients();
        instance.issuer = tokenConfig.getIssuer();
        instance.secretKey = tokenConfig.getSecretKey();
    }

    public static TokenConfig initInstance(String path) throws IOException {

        if (StringUtils.isBlank(path)) {
            path = PATH_FILE_CONFIG.getTokenFileConfigPath();
        }
        instance = ReadFileYAMLUtil.read(path, TokenConfig.class);
        log.info("TokenFileConfig is initiated");
        return instance;
    }
}
