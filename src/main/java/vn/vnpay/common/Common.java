package vn.vnpay.common;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import vn.vnpay.config.TokenClientConfig;
import vn.vnpay.netty.request.TokenRequest;

import java.util.Base64;
import java.util.Objects;

/**
 * @author: DucTN
 * Created: 04/08/2023
 **/
@Slf4j
public class Common {
    private Common() {

    }

    /**
     * to config logback.xml use config from file in @param path
     *
     * @param path: is the absolute path
     */
    public static void configLogback(String path) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        loggerContext.reset();
        try {
            configurator.doConfigure(path);
        } catch (JoranException e) {
            log.error("[configLogback] Config log fails, exception: ", e);
        }
    }

    public static boolean invalidSubClient(TokenRequest request, TokenClientConfig clientConfig) {
        if (Objects.isNull(clientConfig)) {
            log.info("[invalidSubClient] Invalid client id: {}", request.getClientId());
            return true;
        }
        if (!request.getClientId().equals(clientConfig.getClientId())) {
            log.info("[invalidSubClient] Client id is error");
            return true;
        }
        if (!request.getClientSecret().equals(clientConfig.getClientSecret())) {
            log.info("[invalidSubClient] Client Secret is error");
            return true;
        }
        return false;
    }

    public static String decodeBase64(String endCode) {
        try {
            byte[] decodeBytes = Base64.getDecoder().decode(endCode);
            return new String(decodeBytes);
        } catch (Exception e) {
            log.error("[decodeBase64] Decode fails with exception: ", e);
            return null;
        }
    }
}
