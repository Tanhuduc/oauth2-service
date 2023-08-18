package vn.vnpay.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.netty.request.RefreshTokenRequest;
import vn.vnpay.netty.response.Response;
import vn.vnpay.netty.response.data.OAuthToken;
import vn.vnpay.usecase.OAuth2UseCase;

import java.util.Objects;

import static vn.vnpay.netty.Error.Error.CLIENT_INVALID;

/**
 * @Author: DucTN
 * Created: 18/08/2023
 **/
@Slf4j
public class RefreshTokenController implements OAuthController{
   private static RefreshTokenController instance;

   public static RefreshTokenController getInstance() {
      if (Objects.isNull(instance)) {
         instance = new RefreshTokenController();
      }
      return instance;
   }

   private final OAuth2UseCase oAuth2UseCase = OAuth2UseCase.getInstance();
   private final Gson gson = GsonCommon.getInstance();
   @Override
   public Response<Object> handler(String jsonRequest) {
      Response<Object> response = new Response<>();
      RefreshTokenRequest request = gson.fromJson(jsonRequest, RefreshTokenRequest.class);
      log.info("Start refresh token for userId: {}", request.getUserId());
      Triple<String, String, String> result = oAuth2UseCase.refreshToken(request);
      log.info("Finish refresh token");
      if (Objects.isNull(result)) {
         response.setCode(CLIENT_INVALID.getCode());
         response.setCode(CLIENT_INVALID.getMessage());
         return response;
      }
      response.setCode(result.getLeft());
      response.setMessage(result.getMiddle());
      response.setData(result.getRight());
      return response;
   }
}
