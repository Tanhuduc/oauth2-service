package vn.vnpay.controller;

import vn.vnpay.netty.response.Response;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
public interface Controller {
   Response<Object> handler(String jsonRequest);
}
