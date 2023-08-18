package vn.vnpay.netty.Error;

import lombok.Getter;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Getter
public enum Error {
   SUCCESS("200000", "Success"),
   GENERATE_TOKEN_ERROR("400001", "Bad request!"),
   TOKEN_NOT_YET("400002", "Token not yet"),
   TOKEN_EXPIRED("400003", "Token is expired"),
   TOKEN_VALIDATE_ERROR("400004", "Validate token is error"),
   CLIENT_INVALID("400005", "Invalid client match"),
   ACCESS_TOKEN_INVALID("400006", "Invalid access token"),
   REFRESH_TOKEN_INVALID("400007", "Invalid refresh token"),
   API_INVALID("404001", "API invalid"),
   UNKNOWN_ERROR("500000", "Unknown error"),
   ;
   private final String code;
   private final String message;
   Error(String errorCode, String errorMessage) {
      this.code = errorCode;
      this.message = errorMessage;
   }
}
