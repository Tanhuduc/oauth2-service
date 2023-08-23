package vn.vnpay.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
 * Created: 22/08/2023
 **/
@Slf4j
public class ReadFileYAMLUtil {
    private static final Yaml YAML = YamlCommon.getInstance();
    private static final ObjectMapper MAPPER = ObjectMapperCommon.getInstance();

    private ReadFileYAMLUtil() {
    }

    public static <T> T read(String path, Class<T> toValueType) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            Map<String, Object> objectsMap = YAML.load(inputStream);
            T object = MAPPER.convertValue(objectsMap, toValueType);
            log.info("Read file success");
            return object;
        } finally {
            if (!Objects.isNull(inputStream)) {
                inputStream.close();
            }
        }
    }
}
