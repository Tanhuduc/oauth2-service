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

@Slf4j
@Getter
public class RedisConfig {
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private int minEvicIdleTimeSec;
    private int timeBetweenEvicRunsSec;
    private int port;
    private String host;

    private RedisConfig() {
    }

    private static final Yaml YAML = YamlCommon.getInstance();
    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static final ObjectMapper MAPPER = ObjectMapperCommon.getInstance();
    private static RedisConfig instance;

    public static RedisConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = initInstance(PATH_FILE_CONFIG.getRedisFileConfigPath());
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static RedisConfig initInstance(String path) throws IOException {
        InputStream inputStream = null;
        try {
            if (StringUtils.isBlank(path)) {
                path = PATH_FILE_CONFIG.getDbFileConfigPath();
            }
            inputStream = new FileInputStream(path);
            Map<String, Object> objectsMap = YAML.load(inputStream);
            instance = MAPPER.convertValue(objectsMap, RedisConfig.class);
            log.info("RedisConfig is initiated");
            return instance;
        } finally {
            if (!Objects.isNull(inputStream)) {
                inputStream.close();
            }
        }
    }

}
