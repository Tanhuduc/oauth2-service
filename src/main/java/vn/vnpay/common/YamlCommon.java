package vn.vnpay.common;

import org.yaml.snakeyaml.Yaml;

import java.util.Objects;

/**
 * @author: DucTN
 * Created: 02/08/2023
 **/
public class YamlCommon {
   private static Yaml instance;
   private YamlCommon() {
   }
   public static Yaml getInstance() {
       if (Objects.isNull(instance)) {
           instance = new Yaml();
       }
       return instance;
   }
}
