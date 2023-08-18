package vn.vnpay.netty.request;

import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Getter
public class GenerateTokenRequest extends TokenRequest{
   private String userName;
   private String password;
}
