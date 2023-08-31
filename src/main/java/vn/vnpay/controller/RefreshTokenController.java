package vn.vnpay.controller;

import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.core.bean.controller.response.Response;
import vn.vnpay.core.controller.Controller;
import vn.vnpay.service.OAuth2Service;

import java.util.Objects;

import static vn.vnpay.bean.constant.HeaderEntity.AUTHORIZATION;
import static vn.vnpay.bean.constant.HeaderEntity.AUTHORIZATION_CODE;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_ID;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_SECRET;
import static vn.vnpay.bean.constant.HeaderEntity.USER_AGENT;
import static vn.vnpay.bean.constant.HeaderEntity.USER_ID;

/**
 * @Author: DucTN
 * Created: 18/08/2023
 **/
@Slf4j
public class RefreshTokenController implements Controller {
    private static RefreshTokenController instance;

    public static RefreshTokenController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RefreshTokenController();
        }
        return instance;
    }

    private final OAuth2Service oAuth2UseCase = OAuth2Service.getInstance();

    @Override
    public Response<Object> handler(String jsonRequest, HttpHeaders headers) {
        String authorizationCode = headers.get(AUTHORIZATION_CODE.getValue());
        Integer userId = Integer.valueOf(headers.get(USER_ID.getValue()));
        String clientId = headers.get(CLIENT_ID.getValue());
        String clientSecret = headers.get(CLIENT_SECRET.getValue());
        String userAgent = headers.get(USER_AGENT.getValue());
        String authorization = headers.get(AUTHORIZATION.getValue());
        log.info("Start refresh token for userId: {}, clientId: {}", userId, clientId);
        Response<Object> response = oAuth2UseCase.refreshToken(clientId, clientSecret, userAgent
                , authorizationCode, authorization, userId);
        log.info("Finish refresh token with message: {}", response.getMessage());
        return response;
    }
}
