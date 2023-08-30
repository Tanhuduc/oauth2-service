package vn.vnpay.repository;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.entity.UserInfo;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class UserInfoRepository {
    private static UserInfoRepository instance;

    public static UserInfoRepository getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UserInfoRepository();
        }
        return instance;
    }

    private UserInfoRepository() {
    }

    public UserInfo findByUserId(Integer userId) throws SQLException {
//        log.info("[findByUserId] Start find");
//        String sqlQuery = new StringBuilder("SELECT * FROM UserInfo WHERE 1")
//                .append(" AND UserID = '").append(userId).append("'")
//                .toString();
//        Statement statement = null;
//        ResultSet resultSet = null;
//        try {
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery(sqlQuery);
//            if (resultSet.next()) {
//                log.info("[findByUserId] Success");
//                return UserInfo.builder()
//                        .id(resultSet.getInt("ID"))
//                        .firstName(resultSet.getString("FirstName"))
//                        .lastName(resultSet.getString("LastName"))
//                        .age(resultSet.getInt("Age"))
//                        .phone(resultSet.getString("Phone"))
//                        .address((resultSet.getString("Address")))
//                        .image(resultSet.getString("Image"))
//                        .userId(resultSet.getInt("UserID"))
//                        .build();
//            }
//            log.info("[findByUserId] Not found");
//            return null;
//        } catch (Exception e) {
//            log.error("[findByUserId] Exception: ", e);
//            return null;
//        } finally {
//            Objects.requireNonNull(resultSet).close();
//            Objects.requireNonNull(statement).close();
//        }
        return null;
    }
}
