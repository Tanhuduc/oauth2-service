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
public class UserInfo {
    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String image;
    private Integer age;
    private Integer userId;
    private String phone;
}
