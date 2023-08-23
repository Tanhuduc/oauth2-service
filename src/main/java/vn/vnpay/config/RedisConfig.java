package vn.vnpay.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.common.util.ReadFileYAMLUtil;

import java.io.IOException;
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

    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
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

    public static RedisConfig initInstance() throws IOException {
        return initInstance(null);
    }

    public static RedisConfig initInstance(String path) throws IOException {

        if (StringUtils.isBlank(path)) {
            path = PATH_FILE_CONFIG.getRedisFileConfigPath();
        }
        instance = ReadFileYAMLUtil.read(path, RedisConfig.class);
        log.info("RedisConfig is initiated");
        return instance;

    }

}
