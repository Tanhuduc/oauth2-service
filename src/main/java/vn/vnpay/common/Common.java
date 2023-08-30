package vn.vnpay.common;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;

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
            log.info("[configLogback] Has error");
            log.error("[configLogback] Config log fails, exception: ", e);
        }
    }

    public static boolean invalidSubClient(String clientId, String clientSecret, Map<String, String> clients) {
        if (!clients.containsKey(clientId)) {
            log.info("[invalidSubClient] Invalid client id: {}", clientId);
            return true;
        }
        String clientSecretExpect = decodeBase64(clients.get(clientId));
        if (!clientSecretExpect.equals(clientSecret)) {
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
            log.info("[decodeBase64] Has error");
            log.error("[decodeBase64] Decode fails with exception: ", e);
            throw new RuntimeException(e);
        }
    }

    private static String encodeBase64(String input) {
        try {
            return Base64.getEncoder().encodeToString(input.getBytes());
        } catch (Exception e) {
            log.info("[encodeBase64] Has error");
            log.error("[encodeBase64] Encode fails with exception: ", e);
            throw new RuntimeException(e);
        }
    }
}
