package vn.vnpay.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.common.util.ReadFileYAMLUtil;

import java.io.IOException;
import java.util.Objects;

@Getter
@Slf4j
public class OAuthServiceConfig {
    private int port;
    private String host;
    private Uris uris;

    private OAuthServiceConfig() {
    }

    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static OAuthServiceConfig instance;

    public static OAuthServiceConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = initInstance(PATH_FILE_CONFIG.getOauth2ServiceFileConfigPath());
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static OAuthServiceConfig initInstance() throws IOException {
        return initInstance(null);
    }

    public static OAuthServiceConfig initInstance(String path) throws IOException {

        if (StringUtils.isBlank(path)) {
            path = PATH_FILE_CONFIG.getOauth2ServiceFileConfigPath();
        }
        instance = ReadFileYAMLUtil.read(path, OAuthServiceConfig.class);
        log.info("OAuthServiceConfig is initiated");
        return instance;

    }

    @Getter
    public static class Uris {
        private String generateTokenUri;
        private String verifyTokenUri;
        private String refreshTokenUri;
        private String revokeTokenUri;
        private String getUserInfoUri;
    }
}
