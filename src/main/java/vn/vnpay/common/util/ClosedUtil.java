package vn.vnpay.common.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 28/08/2023
 **/
@Slf4j
public class ClosedUtil {
   private ClosedUtil() {
   }

   public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
      try {
         if (Objects.nonNull(resultSet)) {
            resultSet.close();
         }
         if (Objects.nonNull(statement)){
            statement.close();
         }
         if ((Objects.nonNull(connection))) {
            connection.close();
         }
      } catch (Exception e) {
         log.info("Has error");
         log.error("Close is error with exception: ", e);
      }
   }
}
