package vn.vnpay.config;

import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Getter
public class TokenClientConfig {
    private String clientId;
    private String clientSecret;
    private Long expireTime;
    private Long refreshExpireTime;
}
