package vn.vnpay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
