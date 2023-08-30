package vn.vnpay.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: DucTN
 * Created: 28/08/2023
 **/
@Getter
@Builder
@Setter
public class LoginSession {
   private String userAgent;
   private OAuthToken accessToken;
   private OAuthToken refreshToken;
}
