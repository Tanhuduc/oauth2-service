package vn.vnpay.netty.response.data;

import lombok.Builder;
import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Getter
@Builder
public class OAuthToken {
   private String accessToken;
   private String refreshToken;
}
