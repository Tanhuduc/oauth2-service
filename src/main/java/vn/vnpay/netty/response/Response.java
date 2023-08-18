package vn.vnpay.netty.response;

import lombok.Setter;

@Setter
public class Response<T> {
    private String code = "200000";
    private String message = "SUCCESS";
    private T data;

    public Response() {
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(T data) {
        this.data = data;
    }
}
