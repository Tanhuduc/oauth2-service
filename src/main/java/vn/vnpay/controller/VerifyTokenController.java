package vn.vnpay.controller;

import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.controller.response.Response;
import vn.vnpay.service.OAuth2Service;

import java.util.Objects;

import static vn.vnpay.bean.constant.HeaderEntity.AUTHORIZATION;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_ID;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_SECRET;
import static vn.vnpay.bean.constant.HeaderEntity.USER_ID;

/**
 * @Author: DucTN
 * Created: 15/08/2023
 **/
@Slf4j
public class VerifyTokenController implements Controller {
    private static VerifyTokenController instance;

    public static VerifyTokenController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new VerifyTokenController();
        }
        return instance;
    }

    private final OAuth2Service oAuth2UseCase = OAuth2Service.getInstance();

    @Override
    public Response<Object> handler(String jsonRequest, HttpHeaders headers) {
        Integer userId = Integer.valueOf(headers.get(USER_ID.getValue()));
        String clientId = headers.get(CLIENT_ID.getValue());
        String clientSecret = headers.get(CLIENT_SECRET.getValue());
        String authorization = headers.get(AUTHORIZATION.getValue());
        log.info("Start verify token for clientId: {}, userId: {}", clientId, userId);
        Response<Object> response = oAuth2UseCase.verifyToken(clientId, clientSecret, userId, authorization);
        log.info("Verify token return result with: {}", response.getMessage());
        return response;
    }
}
