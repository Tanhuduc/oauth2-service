package vn.vnpay.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import vn.vnpay.common.GsonCommon;
import vn.vnpay.netty.request.RevokeTokenRequest;
import vn.vnpay.netty.response.Response;
import vn.vnpay.usecase.OAuth2UseCase;

import java.util.Objects;

import static vn.vnpay.netty.Error.Error.CLIENT_INVALID;

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

   private final OAuth2UseCase oAuth2UseCase = OAuth2UseCase.getInstance();
   private final Gson gson = GsonCommon.getInstance();
   @Override
   public Response<Object> handler(String jsonRequest) {
      Response<Object> response = new Response<>();
      RevokeTokenRequest request = gson.fromJson(jsonRequest, RevokeTokenRequest.class);
      log.info("Start revoke token for userId: {}", request.getUserId());
      Pair<String, String> result = oAuth2UseCase.revokeToken(request);
      if (Objects.isNull(result)) {
         log.info("Revoke token return null");
         response.setCode(CLIENT_INVALID.getCode());
         response.setCode(CLIENT_INVALID.getMessage());
         return response;
      }
      log.info("Finish revoke token with result: {}", result.getRight());
      response.setCode(result.getLeft());
      response.setMessage(result.getRight());
      return response;
   }
}
