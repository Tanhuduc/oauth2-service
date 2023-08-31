package vn.vnpay.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.core.common.util.ReadFileYAMLUtil;
import vn.vnpay.core.config.NettyServiceConfig;

import java.io.IOException;
import java.util.Objects;

@Getter
@Slf4j
public class OAuthServiceConfig extends NettyServiceConfig {
    private Uris uris;

    private OAuthServiceConfig() {
    }

    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static OAuthServiceConfig instance;

    public static OAuthServiceConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = ReadFileYAMLUtil.read(PATH_FILE_CONFIG.getOauth2ServiceFileConfigPath(), OAuthServiceConfig.class);
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Getter
    public static class Uris {
        private String loginUri;
        private String getTokenUri;
        private String verifyTokenUri;
        private String refreshTokenUri;
        private String revokeTokenUri;
        private String getUserInfoUri;
    }
}
