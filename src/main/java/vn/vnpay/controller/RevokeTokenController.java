package vn.vnpay.controller;

import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.controller.request.RevokeTokenRequest;
import vn.vnpay.core.bean.controller.response.Response;
import vn.vnpay.core.common.GsonCommon;
import vn.vnpay.core.controller.Controller;
import vn.vnpay.service.OAuth2Service;

import java.util.Objects;

import static vn.vnpay.bean.constant.HeaderEntity.AUTHORIZATION;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_ID;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_SECRET;
import static vn.vnpay.bean.constant.HeaderEntity.USER_ID;

/**
 * @Author: DucTN
 * Created: 18/08/2023
 **/
@Slf4j
public class RevokeTokenController implements Controller {
    private static RevokeTokenController instance;

    public static RevokeTokenController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new RevokeTokenController();
        }
        return instance;
    }

    private final OAuth2Service oAuth2UseCase = OAuth2Service.getInstance();
    private final Gson gson = GsonCommon.getInstance();

    @Override
    public Response<Object> handler(String jsonRequest, HttpHeaders headers) {
        Integer userId = Integer.valueOf(headers.get(USER_ID.getValue()));
        String clientId = headers.get(CLIENT_ID.getValue());
        String clientSecret = headers.get(CLIENT_SECRET.getValue());
        String authorization = headers.get(AUTHORIZATION.getValue());
        RevokeTokenRequest request = gson.fromJson(jsonRequest, RevokeTokenRequest.class);
        log.info("Start revoke token for userId: {}", request.getUserId());
        Response<Object> response = oAuth2UseCase.revokeToken(request, userId, clientId, clientSecret, authorization);
        log.info("Finish revoke token with result: {}", response.getMessage());
        return response;
    }
}
