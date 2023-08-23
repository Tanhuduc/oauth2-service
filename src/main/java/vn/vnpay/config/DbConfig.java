package vn.vnpay.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.common.util.ReadFileYAMLUtil;

import java.io.IOException;
import java.util.Objects;

@Getter
@Slf4j
public class DbConfig {
    private String userName;
    private String password;
    private String driver;
    private int minConnections;
    private int maxConnections;
    private int maxOpenPreparedStatement;
    private String connectionUrl;

    private DbConfig() {
    }

    private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
    private static DbConfig instance;

    public static DbConfig getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = initInstance(PATH_FILE_CONFIG.getDbFileConfigPath());
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static DbConfig initInstance() throws IOException {
        return initInstance(null);
    }

    public static DbConfig initInstance(String path) throws IOException {
        if (StringUtils.isBlank(path)) {
            path = PATH_FILE_CONFIG.getDbFileConfigPath();
        }
        instance = ReadFileYAMLUtil.read(path, DbConfig.class);
        log.info("DbConfig is initiated");
        return instance;

    }
}
