package vn.vnpay.controller;

import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.controller.request.LoginRequest;
import vn.vnpay.bean.controller.response.Response;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.service.OAuth2Service;

import java.util.Objects;

import static vn.vnpay.bean.constant.HeaderEntity.USER_AGENT;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Slf4j
public class UserLoginController implements Controller {
    private final OAuth2Service oAuth2UseCase = OAuth2Service.getInstance();
    private final Gson gson = GsonCommon.getInstance();

    private static UserLoginController instance;

    public static UserLoginController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UserLoginController();
        }
        return instance;
    }

    @Override
    public Response<Object> handler(String jsonRequest, HttpHeaders headers) {
        LoginRequest request = gson.fromJson(jsonRequest, LoginRequest.class);
        log.info("Start login for userName: {}", request.getUserName());
        Response<Object> response = oAuth2UseCase.login(request, headers.get(USER_AGENT.getValue()));
        log.info("Finish login with message: {}", response.getMessage());
        return response;
    }
}
