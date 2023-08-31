package vn.vnpay;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.common.Common;
import vn.vnpay.common.HealthCheck;
import vn.vnpay.config.OAuthServiceConfig;
import vn.vnpay.config.PathFileConfig;
import vn.vnpay.core.common.GsonCommon;
import vn.vnpay.core.common.ObjectMapperCommon;
import vn.vnpay.core.common.YamlCommon;
import vn.vnpay.core.common.util.ClosedUtil;
import vn.vnpay.core.netty.ChannelInit;
import vn.vnpay.core.netty.NettyService;
import vn.vnpay.cronjob.InitInstanceCronjob;
import vn.vnpay.netty.ApiHandler;

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
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try {
            Common.configLogback(LOG_PATH);
            init();
            InitInstanceCronjob.start();
            if (HealthCheck.health()) {
                bossGroup = new NioEventLoopGroup();
                workerGroup = new NioEventLoopGroup();
                ChannelInit channelInit = new ChannelInit(ApiHandler.getInstance());
                NettyService service = new NettyService(OAuthServiceConfig.getInstance(), bossGroup, workerGroup, channelInit);
                service.start();
            }
        } catch (Exception e) {
            log.info("[main] Has error");
            log.error("[main] Start service fails, exception: ", e);
        } catch (Throwable throwable) {
            log.info("[main] Has error");
            log.error("[main] Start service fails, throwable: ", throwable);
        } finally {
            ClosedUtil.close(bossGroup, workerGroup);
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