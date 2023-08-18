package vn.vnpay.netty.request;

import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 18/08/2023
 **/
@Getter
public class RefreshTokenRequest extends TokenRequest{
   private String userId;
}
