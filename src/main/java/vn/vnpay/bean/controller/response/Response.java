package vn.vnpay.bean.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private String code = "200000";
    private String message = "SUCCESS";
    private T data;
}
