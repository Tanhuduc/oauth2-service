package vn.vnpay.repository;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.entity.User;
import vn.vnpay.config.pool.DBCPDataSource;
import vn.vnpay.core.common.util.ClosedUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Slf4j
public class UserRepository {
    private static UserRepository instance;

    public static UserRepository getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UserRepository();
        }
        return instance;
    }

    private UserRepository() {
    }

    public User findByUserName(String userName) {
        log.info("[findByUserNameAndPassword] Start find");
        String sqlQuery = new StringBuilder("SELECT * FROM Users WHERE UserName = ?").toString();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                log.info("[findByUserNameAndPassword] Success");
                return User.builder()
                        .id(resultSet.getInt("ID"))
                        .useName(resultSet.getString("UserName"))
                        .password(resultSet.getString("Password"))
                        .role(resultSet.getString("Role"))
                        .build();
            }
            log.info("[findByUserId] Not found");
            return null;
        } catch (Exception e) {
            log.info("[findByUserName] Has error");
            log.error("[findByUserName] Exception: ", e);
            return null;
        } finally {
            ClosedUtil.close(connection, preparedStatement, resultSet);
        }
    }
}
