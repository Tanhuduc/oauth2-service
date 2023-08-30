package vn.vnpay.bean.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Getter
@Setter
@Builder
public class User {
    private Integer id;
    private String useName;
    private String password;
    private String role;
}
