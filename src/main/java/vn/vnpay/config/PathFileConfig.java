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

/**
 * @Author: DucTN
 * Created: 02/08/2023
 **/
@Slf4j
@Getter
public class PathFileConfig {
    private String oauth2ServiceFileConfigPath;
    private String dbFileConfigPath;
    private String redisFileConfigPath;
    private String tokenFileConfigPath;

    private static final Yaml YAML = YamlCommon.getInstance();
    private static final ObjectMapper MAPPER = ObjectMapperCommon.getInstance();
    private static final String DEFAULT_PATH = "demo02-api/src/main/resources/application.yaml";
    private static PathFileConfig instance;

    private PathFileConfig() {
    }

    public static PathFileConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = initInstance(DEFAULT_PATH);
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static PathFileConfig initInstance(String path) throws IOException {
        InputStream inputStream = null;
        try {
            if (StringUtils.isBlank(path)) {
                path = DEFAULT_PATH;
            }
            inputStream = new FileInputStream(path);
            Map<String, Object> objectsMap = YAML.load(inputStream);
            instance = MAPPER.convertValue(objectsMap, PathFileConfig.class);
            log.info("PathFileConfig is initiated");
            return instance;
        } finally {
            if (!Objects.isNull(inputStream)) {
                inputStream.close();
            }
        }
    }
}
