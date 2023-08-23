package vn.vnpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDTO {
   private Integer id;
   private String firstName;
   private String lastName;
   private String address;
   private String image;
   private Integer age;
   private Integer userId;
   private String phone;
}
