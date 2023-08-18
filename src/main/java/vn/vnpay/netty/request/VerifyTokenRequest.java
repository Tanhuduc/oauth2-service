package vn.vnpay.netty.request;

import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 15/08/2023
 **/
@Getter
public class VerifyTokenRequest extends TokenRequest{
   private String accessToken;
}
