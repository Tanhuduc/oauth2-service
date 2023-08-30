package vn.vnpay;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.common.Common;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.common.HealthCheck;
import vn.vnpay.common.ObjectMapperCommon;
import vn.vnpay.common.YamlCommon;
import vn.vnpay.config.OAuthServiceConfig;
import vn.vnpay.config.PathFileConfig;
import vn.vnpay.cronjob.InitInstanceCronjob;
import vn.vnpay.netty.NettyService;

import java.io.IOException;

/**
 * @author: DucTN
 * Created: 11/08/2023
 **/
@Slf4j
public class Main {
    private static final String LOG_PATH = "D:\\Study\\java\\oauth2-service\\src\\log\\config\\logback.xml";
    private static final String PATH_FILE_PATH = "D:\\Study\\java\\oauth2-service\\src\\main\\resources\\application.yaml";

    public static void main(String[] args) {
        try {
            Common.configLogback(LOG_PATH);
            init();
            InitInstanceCronjob.start();
            if (HealthCheck.health()) {
                NettyService service = NettyService.getInstance();
                service.start();
            }
        } catch (Exception e) {
            log.info("[main] Has error");
            log.error("[main] Start service fails, exception: ", e);
        } catch (Throwable throwable) {
            log.info("[main] Has error");
            log.error("[main] Start service fails, throwable: ", throwable);
        }
    }

    private static void init() throws IOException {
        log.info("Start init all instance");
        GsonCommon.getInstance();
        ObjectMapperCommon.getInstance();
        YamlCommon.getInstance();
        initConfig();
        log.info("Finish init all instance");
    }

    private static void initConfig() throws IOException {
        log.info("Start init config");
        PathFileConfig.initInstance(PATH_FILE_PATH);
        OAuthServiceConfig.getInstance();
        log.info("Finish init config");
    }
}