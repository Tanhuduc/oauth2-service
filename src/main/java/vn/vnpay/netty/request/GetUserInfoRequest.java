package vn.vnpay.netty.request;

import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Getter
public class GetUserInfoRequest extends VerifyTokenRequest{
   private Integer userId;
}
