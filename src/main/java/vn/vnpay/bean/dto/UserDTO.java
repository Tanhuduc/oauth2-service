package vn.vnpay.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vnpay.bean.entity.Scope;

import java.util.List;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id;
    private String useName;
    private String role;
    private List<Scope> lstScope;
}
