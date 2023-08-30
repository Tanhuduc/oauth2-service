package vn.vnpay.bean.controller.response.data;

import lombok.AllArgsConstructor;

/**
 * @Author: DucTN
 * Created: 28/08/2023
 **/
@AllArgsConstructor
public class LoginResponse {
    private String authCode;
    private Integer userId;
}
