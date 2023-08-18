package vn.vnpay.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import vn.vnpay.common.ObjectMapperCommon;
import vn.vnpay.common.YamlCommon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Getter
@Slf4j
public class OAuthServiceConfig {
    private int port;
    private String host;
    private Uris uris;

    private OAuthServiceConfig() {
    }

    private static final Yaml YAML = YamlCommon.getInstance();
    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static final ObjectMapper MAPPER = ObjectMapperCommon.getInstance();
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
        InputStream inputStream = null;
        try {
            if (StringUtils.isBlank(path)) {
                path = PATH_FILE_CONFIG.getOauth2ServiceFileConfigPath();
            }
            inputStream = new FileInputStream(path);
            Map<String, Object> objectsMap = YAML.load(inputStream);
            instance = MAPPER.convertValue(objectsMap, OAuthServiceConfig.class);
            log.info("ServiceConfig is initiated");
            return instance;
        } finally {
            if (!Objects.isNull(inputStream)) {
                inputStream.close();
            }
        }
    }

    @Getter
    public static class Uris {
        private String generateTokenUri;
        private String verifyTokenUri;
        private String refreshTokenUri;
        private String revokeTokenUri;
    }
}
