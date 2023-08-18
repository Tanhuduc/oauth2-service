package vn.vnpay;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.common.ObjectMapperCommon;
import vn.vnpay.common.YamlCommon;
import vn.vnpay.config.OAuthServiceConfig;
import vn.vnpay.config.PathFileConfig;
import vn.vnpay.netty.NettyService;

import java.io.IOException;

/**
 * @author: DucTN
 * Created: 11/08/2023
 **/
@Slf4j
public class Main {
    private static final String PATH_FILE_PATH = "D:\\Study\\java\\oauth2-service\\src\\main\\resources\\application.yaml";

    public static void main(String[] args) {
        try {
            init();
//            if (HealthCheck.health()) {
                NettyService service = NettyService.getInstance();
                service.start();
//            }
        } catch (Exception e) {
            log.error("Start service fails, exception: ", e);
        } catch (Throwable throwable) {
            log.error("Start service fails, throwable: ", throwable);
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
        OAuthServiceConfig.initInstance();
        log.info("Finish init config");
    }
}