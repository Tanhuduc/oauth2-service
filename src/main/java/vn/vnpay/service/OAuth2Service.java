package vn.vnpay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.bean.LoginSession;
import vn.vnpay.bean.OAuthToken;
import vn.vnpay.bean.constant.ResponseStatus;
import vn.vnpay.bean.controller.request.LoginRequest;
import vn.vnpay.bean.controller.request.RevokeTokenRequest;
import vn.vnpay.bean.controller.response.data.LoginResponse;
import vn.vnpay.bean.controller.response.data.VerifyResponse;
import vn.vnpay.bean.dto.UserDTO;
import vn.vnpay.bean.entity.User;
import vn.vnpay.cache.TokenCache;
import vn.vnpay.common.Common;
import vn.vnpay.common.util.OAuth2TokenUtil;
import vn.vnpay.common.util.mapper.UserMapper;
import vn.vnpay.config.TokenConfig;
import vn.vnpay.core.bean.controller.response.Response;
import vn.vnpay.core.common.GsonCommon;
import vn.vnpay.core.common.ObjectMapperCommon;
import vn.vnpay.repository.ScopeRepository;
import vn.vnpay.repository.UserRepository;

import java.util.Map;
import java.util.Objects;

import static vn.vnpay.bean.constant.ResponseStatus.CLIENT_INVALID;
import static vn.vnpay.bean.constant.ResponseStatus.LOGIN_SESSION_INVALID;
import static vn.vnpay.bean.constant.ResponseStatus.SUCCESS;
import static vn.vnpay.bean.constant.ResponseStatus.UNAUTHORIZED;
import static vn.vnpay.bean.constant.ResponseStatus.UNKNOWN_ERROR;
import static vn.vnpay.bean.constant.ResponseStatus.USER_INVALID;
import static vn.vnpay.bean.constant.TokenType.ACCESS_TOKEN;
import static vn.vnpay.bean.constant.TokenType.REFRESH_TOKEN;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Slf4j
public class OAuth2Service {
    private final TokenConfig tokenConfig = TokenConfig.getInstance();
    private final TokenCache tokenCache = TokenCache.getINSTANCE();
    private final UserRepository userRepository = UserRepository.getInstance();
    private final ScopeRepository scopeRepository = ScopeRepository.getInstance();
    private final Gson gson = GsonCommon.getInstance();
    private final ObjectMapper mapper = ObjectMapperCommon.getInstance();

    private static OAuth2Service instance;

    public static OAuth2Service getInstance() {
        if (Objects.isNull(instance)) {
            instance = new OAuth2Service();
        }
        return instance;
    }

    private OAuth2Service() {
    }

    /**
     * @param clientId:          id of client
     * @param clientSecret:      secret of client
     * @param userAgent:         device info
     * @param authorizationCode: authorization code
     * @param userId:            id of user
     * @return : access token info
     */
    public Response<Object> getToken(String clientId, String clientSecret
            , String userAgent, String authorizationCode, Integer userId) {
        try {
            if (Common.invalidSubClient(clientId, clientSecret, tokenConfig.getClients())) {
                return Response.builder()
                        .code(CLIENT_INVALID.getCode())
                        .message(CLIENT_INVALID.getMessage())
                        .build();
            }
            String strLoginSession = tokenCache.getLoginSession(userId, authorizationCode);
            if (StringUtils.isBlank(strLoginSession)) {
                log.info("[getToken] Invalid session login, userId: {}", userId);
                return Response.builder()
                        .code(LOGIN_SESSION_INVALID.getCode())
                        .message(LOGIN_SESSION_INVALID.getMessage())
                        .build();
            }
            LoginSession session = gson.fromJson(strLoginSession, LoginSession.class);
            if (!session.getUserAgent().equals(userAgent)) {
                log.info("[getToken] User agent is wrong, user agent request: {}, user agent accept: {}", userAgent, session.getUserAgent());
                return Response.builder()
                        .code(LOGIN_SESSION_INVALID.getCode())
                        .message(LOGIN_SESSION_INVALID.getMessage())
                        .build();
            }
            if (OAuth2TokenUtil.invalidateToken(session.getAccessToken().getToken(), clientId, String.valueOf(userId), tokenConfig.getSecretKey(), ACCESS_TOKEN.name())) {
                log.info("[getToken] Token is invalid");
                return Response.builder()
                        .code(UNAUTHORIZED.getCode())
                        .message(UNAUTHORIZED.getMessage())
                        .build();
            }
            return Response.builder()
                    .code(SUCCESS.getCode())
                    .message(SUCCESS.getMessage())
                    .data(session.getAccessToken())
                    .build();
        } catch (Exception e) {
            log.info("[getToken] Has error");
            log.error("[getToken] Exception: ", e);
            return Response.builder()
                    .code(LOGIN_SESSION_INVALID.getCode())
                    .message(LOGIN_SESSION_INVALID.getMessage())
                    .build();
        }
    }

    /**
     * @param request:   login request
     * @param userAgent: device info
     * @return : login response
     */
    public Response<Object> login(LoginRequest request, String userAgent) {
        try {
            User user = userRepository.findByUserName(request.getUserName());
            if (Objects.isNull(user)) {
                return Response.builder()
                        .code(USER_INVALID.getCode())
                        .message(USER_INVALID.getMessage())
                        .build();
            }
            if (!Common.decodeBase64(user.getPassword()).equals(request.getPassword())) {
                log.info("[login] Password wrong, userId: {}", request.getUserName());
                return Response.builder()
                        .code(USER_INVALID.getCode())
                        .message(USER_INVALID.getMessage())
                        .build();
            }
            String authorizationCode = OAuth2TokenUtil.generateAuthorizeCode(user.getId());
            UserDTO userDTO = UserMapper.convertToDTO(user);
            userDTO.setLstScope(scopeRepository.findScopeByUserId(user.getId()));
            LoginSession loginSession = buildLoginSession(userDTO, userAgent);
            tokenCache.saveLoginSession(user.getId(), authorizationCode, loginSession, tokenConfig.getRefreshExpireTime());
            return Response.builder()
                    .code(SUCCESS.getCode())
                    .message(SUCCESS.getMessage())
                    .data(new LoginResponse(authorizationCode, user.getId()))
                    .build();
        } catch (Exception e) {
            log.info("[login] Has error");
            log.error("[login] Exception: ", e);
            return Response.builder()
                    .code(USER_INVALID.getCode())
                    .message(USER_INVALID.getMessage())
                    .build();
        }
    }

    private LoginSession buildLoginSession(UserDTO userDTO, String userAgent) {
        OAuthToken accessToken = OAuth2TokenUtil
                .generateToken(
                        tokenConfig.getIssuer(),
                        tokenConfig.getSecretKey(),
                        ACCESS_TOKEN.name(),
                        tokenConfig.getExpireTime(),
                        userDTO);
        OAuthToken refreshToken = OAuth2TokenUtil
                .generateToken(
                        tokenConfig.getIssuer(),
                        tokenConfig.getSecretKey(),
                        REFRESH_TOKEN.name(),
                        tokenConfig.getRefreshExpireTime(),
                        userDTO);
        return LoginSession.builder()
                .userAgent(userAgent)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * @param clientId:      id of client
     * @param userId:        id of user
     * @param clientSecret:  secret of client
     * @param authorization: token need verify
     * @return : verify response
     */
    public Response<Object> verifyToken(String clientId, String clientSecret, Integer userId, String authorization) {
        try {
            ResponseStatus status = this.validateToken(clientId, clientSecret, userId, authorization, ACCESS_TOKEN.name());
            if (!status.equals(SUCCESS)) {
                return Response.builder()
                        .code(status.getCode())
                        .message(status.getMessage())
                        .build();
            }
            return Response.builder()
                    .code(SUCCESS.getCode())
                    .message(SUCCESS.getMessage())
                    .data(new VerifyResponse(true))
                    .build();
        } catch (Exception e) {
            log.info("[verifyToken] Has error");
            log.error("[verifyToken] Exception: ", e);
            return Response.builder()
                    .code(UNKNOWN_ERROR.getCode())
                    .message(UNKNOWN_ERROR.getMessage())
                    .build();
        }
    }

    public ResponseStatus validateToken(String clientId, String clientSecret, Integer userId, String authorization, String tokenType) {
        if (Common.invalidSubClient(clientId, clientSecret, tokenConfig.getClients())) {
            return CLIENT_INVALID;
        }
        if (OAuth2TokenUtil.invalidateToken(authorization, clientId, String.valueOf(userId), tokenConfig.getSecretKey(), tokenType)) {
            log.info("[validateToken] Token is invalid");
            return UNAUTHORIZED;
        }
        return SUCCESS;
    }

    /**
     * @param clientId:          id of client
     * @param clientSecret:      secret of client
     * @param userAgent:         device info
     * @param authorizationCode: authorization code
     * @param accessToken:       access token
     * @param userId:            id of user
     * @return : access token info
     */
    public Response<Object> refreshToken(String clientId, String clientSecret
            , String userAgent, String authorizationCode, String accessToken, Integer userId) {
        try {
            String strLoginSession = tokenCache.getLoginSession(userId, authorizationCode);
            if (StringUtils.isBlank(strLoginSession)) {
                log.info("[refreshToken] Invalid session login, userId: {}", userId);
                return Response.builder()
                        .code(LOGIN_SESSION_INVALID.getCode())
                        .message(LOGIN_SESSION_INVALID.getMessage())
                        .build();
            }
            LoginSession session = gson.fromJson(strLoginSession, LoginSession.class);
            if (!accessToken.equals(session.getAccessToken().getToken())) {
                log.info("[refreshToken] Access token is invalid");
                return Response.builder()
                        .code(UNAUTHORIZED.getCode())
                        .message(UNAUTHORIZED.getMessage())
                        .build();
            }
            String refreshToken = session.getRefreshToken().getToken();
            ResponseStatus status = this.validateToken(clientId, clientSecret, userId, refreshToken, REFRESH_TOKEN.name());
            if (!status.equals(SUCCESS)) {
                return Response.builder()
                        .code(status.getCode())
                        .message(status.getMessage())
                        .build();
            }
            Claims claims = OAuth2TokenUtil.getClaims(refreshToken, tokenConfig.getSecretKey());
            Map<String, Object> mUserDTO = claims.get(String.valueOf(userId), Map.class);
            if (Objects.isNull(mUserDTO) || mUserDTO.isEmpty()) {
                log.info("[refreshToken] Token is error, not found user info");
                return Response.builder()
                        .code(UNAUTHORIZED.getCode())
                        .message(UNAUTHORIZED.getMessage())
                        .build();
            }
            UserDTO userDTO = mapper.convertValue(mUserDTO, UserDTO.class);
            LoginSession loginSession = buildLoginSession(userDTO, userAgent);
            tokenCache.saveLoginSession(userDTO.getId(), authorizationCode, loginSession, tokenConfig.getRefreshExpireTime());
            return Response.builder()
                    .code(SUCCESS.getCode())
                    .message(SUCCESS.getMessage())
                    .data(loginSession.getAccessToken())
                    .build();
        } catch (Exception e) {
            log.info("[refreshToken] Has error");
            log.error("[refreshToken] Exception: ", e);
            return Response.builder()
                    .code(UNKNOWN_ERROR.getCode())
                    .message(UNKNOWN_ERROR.getMessage())
                    .build();
        }
    }

    /**
     * @param request:       revoke token request (userId: id of user need revoke)
     * @param userId:        id of user
     * @param clientId:      id of client
     * @param clientSecret:  secret of client
     * @param authorization: access token of user
     * @return : result revoke
     */
    public Response<Object> revokeToken(RevokeTokenRequest request, Integer userId
            , String clientId, String clientSecret, String authorization) {
        try {
            ResponseStatus status = this.validateToken(clientId, clientSecret, userId, authorization, ACCESS_TOKEN.name());
            if (!status.equals(SUCCESS)) {
                return Response.builder()
                        .code(status.getCode())
                        .message(status.getMessage())
                        .build();
            }
            Long result = tokenCache.deleteLoginSession(request.getUserId());
            if (1 > result) {
                return Response.builder()
                        .code(LOGIN_SESSION_INVALID.getCode())
                        .message(LOGIN_SESSION_INVALID.getMessage())
                        .build();
            }
            return Response.builder()
                    .code(SUCCESS.getCode())
                    .message(SUCCESS.getMessage())
                    .build();
        } catch (Exception e) {
            log.info("[revokeToken] Has error");
            log.error("[revokeToken] Exception: ", e);
            return Response.builder()
                    .code(UNKNOWN_ERROR.getCode())
                    .message(UNKNOWN_ERROR.getMessage())
                    .build();
        }
    }
}
