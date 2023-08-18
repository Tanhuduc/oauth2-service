package vn.vnpay.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Getter
@Setter
@NoArgsConstructor
public class UserInfo {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String image;
    private Integer age;
    private String userId;
    private String phone;
}
