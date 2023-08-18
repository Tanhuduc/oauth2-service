package vn.vnpay.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.netty.request.GenerateTokenRequest;
import vn.vnpay.netty.response.Response;
import vn.vnpay.netty.response.data.OAuthToken;
import vn.vnpay.usecase.OAuth2UseCase;

import java.util.Objects;

import static vn.vnpay.netty.Error.Error.GENERATE_TOKEN_ERROR;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Slf4j
public class GenerateTokenController implements OAuthController {
    private static GenerateTokenController instance;

    public static GenerateTokenController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new GenerateTokenController();
        }
        return instance;
    }

    private final OAuth2UseCase oAuth2UseCase = OAuth2UseCase.getInstance();
    private final Gson gson = GsonCommon.getInstance();

    @Override
    public Response<Object> handler(String jsonRequest) {
        GenerateTokenRequest request = gson.fromJson(jsonRequest, GenerateTokenRequest.class);
        log.info("Start generate token for clientId: {}", request.getClientId());
        String accessToken = oAuth2UseCase.generateToken(request);
        log.info("Finish generate token");
        if (StringUtils.isBlank(accessToken)) {
            Response<Object> response = new Response<>();
            response.setCode(GENERATE_TOKEN_ERROR.getCode());
            response.setMessage(GENERATE_TOKEN_ERROR.getMessage());
            return response;
        }
        return new Response<>(accessToken);
    }
}
