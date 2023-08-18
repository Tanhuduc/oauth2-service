package vn.vnpay.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import vn.vnpay.cache.TokenCache;
import vn.vnpay.common.ObjectMapperCommon;
import vn.vnpay.dto.UserDTO;
import vn.vnpay.netty.response.data.OAuthToken;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static vn.vnpay.netty.Error.Error.ACCESS_TOKEN_INVALID;
import static vn.vnpay.netty.Error.Error.REFRESH_TOKEN_INVALID;
import static vn.vnpay.netty.Error.Error.SUCCESS;
import static vn.vnpay.netty.Error.Error.TOKEN_EXPIRED;
import static vn.vnpay.netty.Error.Error.TOKEN_NOT_YET;
import static vn.vnpay.netty.Error.Error.TOKEN_VALIDATE_ERROR;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Slf4j
public class OAuth2TokenUtil {
    private static final TokenCache TOKEN_CACHE = TokenCache.getINSTANCE();
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperCommon.getInstance();
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String TOKEN_TYPE = "tokenType";

    private OAuth2TokenUtil() {
    }

    /**
     * @param issuer:                  path url of auth-service
     * @param secretKey:               key to gen token
     * @param clientId:                id of client request token
     * @param expirationTimeIn:        time to accessToken expire
     * @param refreshExpirationTimeIn: time to refreshToken expire
     * @param userDTO:                 user info
     * @return: OAuthToken
     */
    public static OAuthToken generateToken(
            String issuer, String secretKey, String clientId,
            Long expirationTimeIn, Long refreshExpirationTimeIn, UserDTO userDTO) {
        Date issueAt = new Date();
        Date expirationAt = new Date(issueAt.getTime() + expirationTimeIn);
        Date refreshExpirationAt = new Date(issueAt.getTime() + refreshExpirationTimeIn);
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        try {
            String accessToken = Jwts.builder()
                    .setIssuer(issuer)
                    .claim(clientId, userDTO)
                    .claim(TOKEN_TYPE, ACCESS_TOKEN)
                    .setIssuedAt(issueAt)
                    .setExpiration(expirationAt)
                    .signWith(key)
                    .compact();
            String refreshToken = Jwts.builder()
                    .setIssuer(issuer)
                    .claim(clientId, userDTO)
                    .claim(TOKEN_TYPE, REFRESH_TOKEN)
                    .setIssuedAt(issueAt)
                    .setExpiration(refreshExpirationAt)
                    .signWith(key)
                    .compact();
            return OAuthToken.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Exception: ", e);
            return null;
        }
    }

    /**
     * @param accessToken: token need to validate
     * @param secretKey:   key to validate
     * @return Triple: message(L), result validate(M), code(R)
     */
    public static Triple<String, Boolean, String> validateAccessToken(String accessToken, String secretKey, String clientId) {
        try {
            long timeNow = (new Date()).getTime();
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            Claims claims = claimsJws.getBody();
            if (!ACCESS_TOKEN.equals(claims.get(TOKEN_TYPE).toString())) {
                log.info("Token type not access token");
                return Triple.of(ACCESS_TOKEN_INVALID.getMessage(), false, ACCESS_TOKEN_INVALID.getCode());
            }
            if (claims.getIssuedAt().getTime() > timeNow) {
                log.info("Issue date not yet");
                return Triple.of(TOKEN_NOT_YET.getMessage(), false, TOKEN_NOT_YET.getCode());
            }
            var userDTO = claims.get(clientId, Map.class);
            if (Objects.isNull(userDTO)) {
                log.info("Token is error");
                return Triple.of(TOKEN_VALIDATE_ERROR.getMessage(), false, TOKEN_NOT_YET.getCode());
            }
            log.info("Validate token success for user: {}", userDTO);
            return Triple.of(SUCCESS.getMessage(), true, SUCCESS.getCode());
        } catch (Exception e) {
            log.error("[validateAccessToken] Validate is error, Exception: ", e);
            if (e instanceof ExpiredJwtException) {
                log.info("Token has expired");
                return Triple.of(TOKEN_EXPIRED.getMessage(), false, TOKEN_EXPIRED.getCode());
            }
            return Triple.of(TOKEN_VALIDATE_ERROR.getMessage(), false, TOKEN_VALIDATE_ERROR.getCode());
        }
    }

    /**
     * @param clientId:                id of client request token
     * @param userId:                  user id
     * @param secretKey:               key to gen token
     * @param issuer:                  path url of auth-service
     * @param expirationTimeIn:        time to accessToken expire
     * @param refreshExpirationTimeIn: time to refreshToken expire
     * @return: Triple: code(L), message(M), OAuthToken(R)
     */
    public static Triple<String, String, OAuthToken> refreshToken(String clientId, String userId, String secretKey,
                                                                  String issuer, Long expirationTimeIn, Long refreshExpirationTimeIn) {
        try {
            String refreshToken = TOKEN_CACHE.getRefreshToken(clientId, userId);
            if (StringUtils.isBlank(refreshToken)) {
                log.info("Invalid refresh token for userId: {}", userId);
                return Triple.of(REFRESH_TOKEN_INVALID.getCode(), REFRESH_TOKEN_INVALID.getMessage(), null);
            }
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
            Claims claims = claimsJws.getBody();
            if (!REFRESH_TOKEN.equals(claims.get(TOKEN_TYPE).toString())) {
                log.info("Token type not refresh token");
                return Triple.of(REFRESH_TOKEN_INVALID.getCode(), REFRESH_TOKEN_INVALID.getMessage(), null);
            }
            var user = claims.get(clientId, Map.class);
            if (Objects.isNull(user)) {
                log.info("Token is error");
                return Triple.of(REFRESH_TOKEN_INVALID.getCode(), TOKEN_VALIDATE_ERROR.getMessage(), null);
            }
            UserDTO userDTO = OBJECT_MAPPER.convertValue(user, UserDTO.class);
            OAuthToken oAuthToken = generateToken(issuer, secretKey, clientId, expirationTimeIn, refreshExpirationTimeIn, userDTO);
            return Triple.of(SUCCESS.getCode(), SUCCESS.getMessage(), oAuthToken);
        } catch (Exception e) {
            log.error("[validateAccessToken] Validate is error, Exception: ", e);
            if (e instanceof ExpiredJwtException) {
                log.info("Token has expired");
                return Triple.of(TOKEN_EXPIRED.getCode(), TOKEN_EXPIRED.getMessage(), null);
            }
            return Triple.of(TOKEN_VALIDATE_ERROR.getCode(), TOKEN_VALIDATE_ERROR.getMessage(), null);
        }
    }
}
