package vn.vnpay.bean;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Getter
@Builder
public class OAuthToken {
   private String token;
   private Date expireTime;
}
