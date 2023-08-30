package vn.vnpay.controller;

import io.netty.handler.codec.http.HttpHeaders;
import vn.vnpay.bean.controller.response.Response;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
public interface Controller {
    Response<Object> handler(String jsonRequest, HttpHeaders headers);
}
