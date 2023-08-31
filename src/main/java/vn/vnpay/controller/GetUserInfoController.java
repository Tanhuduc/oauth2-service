package vn.vnpay.controller;

import io.netty.handler.codec.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.constant.ResponseStatus;
import vn.vnpay.core.bean.controller.response.Response;
import vn.vnpay.core.controller.Controller;
import vn.vnpay.service.OAuth2Service;
import vn.vnpay.service.UserInfoService;

import java.util.Objects;

import static vn.vnpay.bean.constant.HeaderEntity.AUTHORIZATION;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_ID;
import static vn.vnpay.bean.constant.HeaderEntity.CLIENT_SECRET;
import static vn.vnpay.bean.constant.HeaderEntity.USER_ID;
import static vn.vnpay.bean.constant.ResponseStatus.SUCCESS;
import static vn.vnpay.bean.constant.TokenType.ACCESS_TOKEN;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class GetUserInfoController implements Controller {
    private final OAuth2Service oAuth2Service = OAuth2Service.getInstance();
    private final UserInfoService userInfoService = UserInfoService.getInstance();

    private static GetUserInfoController instance;

    public static GetUserInfoController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new GetUserInfoController();
        }
        return instance;
    }

    private GetUserInfoController() {
    }

    @Override
    public Response<Object> handler(String jsonRequest, HttpHeaders headers) {
        Integer userId = Integer.valueOf(headers.get(USER_ID.getValue()));
        String clientId = headers.get(CLIENT_ID.getValue());
        String clientSecret = headers.get(CLIENT_SECRET.getValue());
        String authorization = headers.get(AUTHORIZATION.getValue());
        ResponseStatus status = oAuth2Service.validateToken(clientId, clientSecret, userId, authorization, ACCESS_TOKEN.name());
        if (!status.equals(SUCCESS)) {
            return Response.builder()
                    .code(status.getCode())
                    .message(status.getMessage())
                    .build();
        }
        Response<Object> response = userInfoService.getUserInfo(userId);
        log.info("Find user info return result with message: {}", response.getMessage());
        return response;
    }
}
