package vn.vnpay.controller;

import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.core.bean.controller.response.Response;
import vn.vnpay.core.controller.Controller;
import vn.vnpay.service.OAuth2Service;

import java.util.Objects;

import static vn.vnpay.bean.constant.HeaderEntity.AUTHORIZATION_CODE;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_ID;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_SECRET;
import static vn.vnpay.bean.constant.HeaderEntity.USER_AGENT;
import static vn.vnpay.bean.constant.HeaderEntity.USER_ID;

/**
 * @Author: DucTN
 * Created: 29/08/2023
 **/
@Slf4j
public class GetTokenController implements Controller {
    private final OAuth2Service oAuth2UseCase = OAuth2Service.getInstance();

    private static GetTokenController instance;

    public static GetTokenController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new GetTokenController();
        }
        return instance;
    }

    @Override
    public Response<Object> handler(String jsonRequest, HttpHeaders headers) {
        Integer userId = Integer.valueOf(headers.get(USER_ID.getValue()));
        String clientId = headers.get(CLIENT_ID.getValue());
        String clientSecret = headers.get(CLIENT_SECRET.getValue());
        String userAgent = headers.get(USER_AGENT.getValue());
        String authorizationCode = headers.get(AUTHORIZATION_CODE.getValue());
        log.info("Start get token for userId: {}, clientId: {}", userId, clientId);
        Response<Object> response = oAuth2UseCase.getToken(clientId, clientSecret, userAgent, authorizationCode, userId);
        log.info("Finish get token with message: {}", response.getMessage());
        return response;
    }
}
