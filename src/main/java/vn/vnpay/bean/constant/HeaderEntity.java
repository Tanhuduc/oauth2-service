package vn.vnpay.bean.constant;

import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 29/08/2023
 **/
@Getter
public enum HeaderEntity {
   USER_AGENT("User-Agent"),
   CLIENT_ID("client-id"),
   CLIENT_SECRET("client-secret"),
   AUTHORIZATION("Authorization"),
   AUTHORIZATION_CODE("Authorization-Code"),
   USER_ID("user-id");
   private final String value;
   HeaderEntity(String value) {
      this.value = value;
   }
}
