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
public class User {
    private String id;
    private String useName;
    private int password;
    private String role;
}
