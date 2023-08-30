package vn.vnpay.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
                instance = ReadFileYAMLUtil.read(PATH_FILE_CONFIG.getDbFileConfigPath(), DbConfig.class);
            } catch (IOException e) {
                log.error("Init instance fails, exception: ", e);
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

}
