package vn.vnpay.repository;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public User findByUserNameAndPassword(Connection connection, String userName, String password) throws SQLException {
        log.info("[findByUserNameAndPassword] Start find");
        String sqlQuery = new StringBuilder("SELECT * FROM Users WHERE 1")
                .append(" AND UserName = '").append(userName).append("'")
                .append(" AND Password = '").append(password).append("'")
                .toString();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
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
            log.error("[findByUserNameAndPassword] Exception: ", e);
            return null;
        } finally {
            Objects.requireNonNull(resultSet).close();
            Objects.requireNonNull(statement).close();
        }
    }
}
