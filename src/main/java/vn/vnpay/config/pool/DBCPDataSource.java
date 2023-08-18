package vn.vnpay.config.pool;

import org.apache.commons.dbcp2.BasicDataSource;
import vn.vnpay.config.DbConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class DBCPDataSource {
    private static final DbConfig CONFIG = DbConfig.getInstance();
    private static final BasicDataSource BASIC_DATA_SOURCE = new BasicDataSource();

    static {
        BASIC_DATA_SOURCE.setDriverClassName(CONFIG.getDriver());
        BASIC_DATA_SOURCE.setUrl(CONFIG.getConnectionUrl());
        BASIC_DATA_SOURCE.setUsername(CONFIG.getUserName());
        BASIC_DATA_SOURCE.setPassword(CONFIG.getPassword());
        BASIC_DATA_SOURCE.setMinIdle(CONFIG.getMinConnections()); // minimum number of idle connections in the pool
        BASIC_DATA_SOURCE.setInitialSize(CONFIG.getMinConnections());
        BASIC_DATA_SOURCE.setMaxIdle(CONFIG.getMaxConnections()); // maximum number of idle connections in the pool
        BASIC_DATA_SOURCE.setMaxOpenPreparedStatements(CONFIG.getMaxOpenPreparedStatement());//the new maximum number of prepared statements
    }

    private DBCPDataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return BASIC_DATA_SOURCE.getConnection();
    }
}
