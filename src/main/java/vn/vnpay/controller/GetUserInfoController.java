package vn.vnpay.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.dto.UserInfoDTO;
import vn.vnpay.netty.request.GetUserInfoRequest;
import vn.vnpay.netty.response.Response;
import vn.vnpay.usecase.OAuth2UseCase;
import vn.vnpay.usecase.UserInfoUseCase;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class GetUserInfoController implements Controller {
    private final OAuth2UseCase oAuth2UseCase = OAuth2UseCase.getInstance();
    private final UserInfoUseCase userInfoUseCase = UserInfoUseCase.getInstance();
    private final Gson gson = GsonCommon.getInstance();

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
    public Response<Object> handler(String jsonRequest) {
        GetUserInfoRequest request = gson.fromJson(jsonRequest, GetUserInfoRequest.class);
        Response<Object> response = new Response<>();

        Triple<String, String, Boolean> resultVerifyToken = oAuth2UseCase.verifyToken(request);
        if (Boolean.FALSE.equals(resultVerifyToken.getRight())) {
            log.info("Verify token fails, code: {}, message: {}", resultVerifyToken.getLeft(), resultVerifyToken.getMiddle());
            response.setCode(resultVerifyToken.getLeft());
            response.setMessage(resultVerifyToken.getMiddle());
            return response;
        }
        Triple<String, String, UserInfoDTO> result = userInfoUseCase.getUserInfo(request);
        log.info("Find user info return result: code: {}, message: {}", resultVerifyToken.getLeft(), resultVerifyToken.getMiddle());
        response.setCode(result.getLeft());
        response.setMessage(result.getMiddle());
        response.setData(result.getRight());
        return response;
    }
}
