package vn.vnpay.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.netty.request.VerifyTokenRequest;
import vn.vnpay.netty.response.Response;
import vn.vnpay.usecase.OAuth2UseCase;

import java.util.Objects;

import static vn.vnpay.netty.Error.Error.CLIENT_INVALID;

/**
 * @Author: DucTN
 * Created: 15/08/2023
 **/
@Slf4j
public class VerifyTokenController implements OAuthController {
    private static VerifyTokenController instance;

    public static VerifyTokenController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new VerifyTokenController();
        }
        return instance;
    }

    private final OAuth2UseCase oAuth2UseCase = OAuth2UseCase.getInstance();
    private final Gson gson = GsonCommon.getInstance();

    @Override
    public Response<Object> handler(String jsonRequest) {
        Response<Object> response = new Response<>();
        VerifyTokenRequest request = gson.fromJson(jsonRequest, VerifyTokenRequest.class);
        log.info("Start verify token for clientId: {}", request.getClientId());
        Triple<String, Boolean, String> result = oAuth2UseCase.verifyToken(request);
        log.info("Finish verify token");
        if (Objects.isNull(result)) {
            response.setCode(CLIENT_INVALID.getCode());
            response.setCode(CLIENT_INVALID.getMessage());
            return response;
        }
        response.setCode(result.getRight());
        response.setMessage(result.getLeft());
        response.setData(result.getMiddle());
        return response;
    }
}