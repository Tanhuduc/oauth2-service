package vn.vnpay.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: DucTN
 * Created: 28/08/2023
 **/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scope {
    private Integer id;
    private String scope;
}
