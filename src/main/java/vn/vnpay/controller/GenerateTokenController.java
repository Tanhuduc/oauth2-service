package vn.vnpay.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.netty.request.GenerateTokenRequest;
import vn.vnpay.netty.response.Response;
import vn.vnpay.usecase.OAuth2UseCase;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Slf4j
public class GenerateTokenController implements Controller {
    private final OAuth2UseCase oAuth2UseCase = OAuth2UseCase.getInstance();
    private final Gson gson = GsonCommon.getInstance();

    private static GenerateTokenController instance;

    public static GenerateTokenController getInstance() {
        if (Objects.isNull(instance)) {
            instance = new GenerateTokenController();
        }
        return instance;
    }

    @Override
    public Response<Object> handler(String jsonRequest) {
        GenerateTokenRequest request = gson.fromJson(jsonRequest, GenerateTokenRequest.class);
        Response<Object> response = new Response<>();
        log.info("Start generate token for clientId: {}, userName: {}", request.getClientId(), request.getUserName());
        Triple<String, String, String> result = oAuth2UseCase.generateToken(request);
        log.info("Finish generate token with result message: {}", result.getMiddle());
        response.setCode(result.getLeft());
        response.setMessage(result.getMiddle());
        response.setData(result.getRight());
        return response;
    }
}
