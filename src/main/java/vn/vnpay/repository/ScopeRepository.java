package vn.vnpay.repository;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.entity.Scope;
import vn.vnpay.common.util.ClosedUtil;
import vn.vnpay.config.pool.DBCPDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 30/08/2023
 **/
@Slf4j
public class ScopeRepository {
    private static ScopeRepository instance;

    public static ScopeRepository getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ScopeRepository();
        }
        return instance;
    }

    private ScopeRepository() {
    }

    public List<Scope> findScopeByUserId(Integer userId) {
        log.info("[findScopeByUserId] Start find");
        String sqlQuery = new StringBuilder("SELECT s.ID, s.Scope FROM ScopeUser su JOIN Scope s WHERE su.UserID = ?").toString();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Scope> lstScope = new ArrayList<>();
        try {
            connection = DBCPDataSource.getConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                lstScope.add(Scope.builder()
                        .id(resultSet.getInt("ID"))
                        .scope(resultSet.getString("Scope"))
                        .build());
            }
            log.info("[findScopeByUserId] Success");
            return lstScope;
        } catch (Exception e) {
            log.info("[findScopeByUserId] Has error");
            log.error("[findScopeByUserId] Exception: ", e);
            throw new RuntimeException(e);
        } finally {
            ClosedUtil.close(connection, preparedStatement, resultSet);
        }
    }
}
