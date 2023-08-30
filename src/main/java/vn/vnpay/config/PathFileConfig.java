package vn.vnpay.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.common.util.ReadFileYAMLUtil;

import java.io.IOException;
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

    private static final String DEFAULT_PATH = "demo02-api/src/main/resources/application.yaml";
    private static PathFileConfig instance;

    private PathFileConfig() {
    }

    public static PathFileConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = ReadFileYAMLUtil.read(DEFAULT_PATH, PathFileConfig.class);
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static PathFileConfig initInstance(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            path = DEFAULT_PATH;
        }
        instance = ReadFileYAMLUtil.read(path, PathFileConfig.class);
        log.info("PathFileConfig is initiated");
        return instance;
    }
}
