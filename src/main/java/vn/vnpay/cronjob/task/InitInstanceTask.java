package vn.vnpay.cronjob.task;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.config.DbConfig;
import vn.vnpay.config.OAuthServiceConfig;
import vn.vnpay.config.RedisConfig;
import vn.vnpay.config.TokenConfig;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class InitInstanceTask implements Runnable{
   private static InitInstanceTask instance;

   public static InitInstanceTask getInstance() {
      if (Objects.isNull(instance)) {
         instance = new InitInstanceTask();
      }
      return instance;
   }

   private InitInstanceTask() {
   }

   @Override
   public void run() {
      try {
         log.info("Init instance");
         TokenConfig.refreshInstance();
      } catch (Exception e) {
         log.info("Exception: ", e);
      }
   }
}
