package vn.vnpay.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import vn.vnpay.common.ObjectMapperCommon;
import vn.vnpay.common.YamlCommon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Slf4j
@Getter
public class TokenConfig {
   private String secretKey;
   private String issuer;
   private Map<String, TokenClientConfig> clients;
   private TokenConfig() {
   }

   private static final Yaml YAML = YamlCommon.getInstance();
   private static final PathFileConfig PATH_FILE_CONFIG = PathFileConfig.getInstance();
   private static final ObjectMapper MAPPER = ObjectMapperCommon.getInstance();
   private static TokenConfig instance;

   public static TokenConfig getInstance() {
      if (Objects.isNull(instance)) {
         try {
            instance = initInstance(PATH_FILE_CONFIG.getTokenFileConfigPath());
         } catch (IOException e) {
            log.error("Init instance fails, exception: ", e);
            throw new RuntimeException(e);
         }
      }
      return instance;
   }

   public static TokenConfig initInstance(String path) throws IOException {
      InputStream inputStream = null;
      try {
         if (StringUtils.isBlank(path)) {
            path = PATH_FILE_CONFIG.getTokenFileConfigPath();
         }
         inputStream = new FileInputStream(path);
         Map<String, Object> objectsMap = YAML.load(inputStream);
         instance = MAPPER.convertValue(objectsMap, TokenConfig.class);
         log.info("TokenFileConfig is initiated");
         return instance;
      } finally {
         if (!Objects.isNull(inputStream)) {
            inputStream.close();
         }
      }
   }
}
