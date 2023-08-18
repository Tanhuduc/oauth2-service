package vn.vnpay.netty.request;

import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 15/08/2023
 **/
@Getter
public class TokenRequest {
   private String clientId;
   private String clientSecret;
}
