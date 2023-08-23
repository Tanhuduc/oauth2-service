package vn.vnpay.usecase;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import vn.vnpay.cache.TokenCache;
import vn.vnpay.common.Common;
import vn.vnpay.common.util.OAuth2TokenUtil;
import vn.vnpay.config.TokenClientConfig;
import vn.vnpay.config.TokenConfig;
import vn.vnpay.config.pool.DBCPDataSource;
import vn.vnpay.dto.UserDTO;
import vn.vnpay.mapper.UserMapper;
import vn.vnpay.netty.request.GenerateTokenRequest;
import vn.vnpay.netty.request.RefreshTokenRequest;
import vn.vnpay.netty.request.RevokeTokenRequest;
import vn.vnpay.netty.request.VerifyTokenRequest;
import vn.vnpay.netty.response.data.OAuthToken;
import vn.vnpay.repository.UserRepository;

import java.sql.Connection;
import java.util.Objects;

import static vn.vnpay.netty.Error.Error.CLIENT_INVALID;
import static vn.vnpay.netty.Error.Error.GENERATE_TOKEN_ERROR;
import static vn.vnpay.netty.Error.Error.REFRESH_TOKEN_INVALID;
import static vn.vnpay.netty.Error.Error.SUCCESS;
import static vn.vnpay.netty.Error.Error.USER_INVALID;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
@Slf4j
public class OAuth2UseCase {
    private final TokenConfig tokenConfig = TokenConfig.getInstance();
    private final TokenCache tokenCache = TokenCache.getINSTANCE();
    private final UserRepository userRepository = UserRepository.getInstance();
    private final UserMapper userMapper = UserMapper.getInstance();

    private static OAuth2UseCase instance;

    public static OAuth2UseCase getInstance() {
        if (Objects.isNull(instance)) {
            instance = new OAuth2UseCase();
        }
        return instance;
    }

    private OAuth2UseCase() {
    }

    /**
     * @param request: generate token request
     * @return: Triple: code(L), message(M), accessToken(R)
     */
    public Triple<String, String, String> generateToken(GenerateTokenRequest request) {
        Connection connection;
        try {
            connection = DBCPDataSource.getConnection();
            TokenClientConfig client = tokenConfig.getClients().get(request.getClientId());
            if (Common.invalidSubClient(request, client)) {
                return Triple.of(CLIENT_INVALID.getCode(), CLIENT_INVALID.getMessage(), null);
            }
            UserDTO userDTO = userMapper.convertToDTO(
                    userRepository.findByUserNameAndPassword(connection, request.getUserName(), request.getPassword())
            );
            if (Objects.isNull(userDTO)) {
                return Triple.of(USER_INVALID.getCode(), USER_INVALID.getMessage(), null);
            }

            OAuthToken oAuthToken = OAuth2TokenUtil
                    .generateToken(
                            tokenConfig.getIssuer(),
                            tokenConfig.getSecretKey(),
                            client.getClientId(),
                            client.getExpireTime(),
                            client.getRefreshExpireTime(),
                            userDTO);
            if (Objects.isNull(oAuthToken)) {
                return Triple.of(GENERATE_TOKEN_ERROR.getCode(), GENERATE_TOKEN_ERROR.getMessage(), null);
            }
            tokenCache.saveRefreshToken(request.getClientId(), userDTO.getId(), oAuthToken.getRefreshToken());
            connection.close();
            return Triple.of(SUCCESS.getCode(), SUCCESS.getMessage(), oAuthToken.getAccessToken());
        } catch (Exception e) {
            log.error("[generateToken] Exception: ", e);
            return Triple.of(GENERATE_TOKEN_ERROR.getCode(), GENERATE_TOKEN_ERROR.getMessage(), null);
        }

    }

    /**
     * @param request: verify token request
     * @return Triple: code(R), message(L), result validate(M)
     */
    public Triple<String, String, Boolean> verifyToken(VerifyTokenRequest request) {
        TokenClientConfig client = tokenConfig.getClients().get(request.getClientId());
        if (Common.invalidSubClient(request, client)) {
            return Triple.of(CLIENT_INVALID.getCode(), CLIENT_INVALID.getMessage(), false);
        }
        return OAuth2TokenUtil
                .validateAccessToken(request.getAccessToken(), tokenConfig.getSecretKey(), request.getClientId());
    }

    /**
     * @param request: Refresh token request
     * @return: Triple: code(L), message(M), OAuthToken(R)
     */
    public Triple<String, String, String> refreshToken(RefreshTokenRequest request) {
        TokenClientConfig client = tokenConfig.getClients().get(request.getClientId());
        if (Common.invalidSubClient(request, client)) {
            return Triple.of(CLIENT_INVALID.getCode(), CLIENT_INVALID.getMessage(), null);
        }
        Triple<String, String, OAuthToken> result = OAuth2TokenUtil.refreshToken(request.getClientId()
                , request.getUserId()
                , tokenConfig.getSecretKey()
                , tokenConfig.getIssuer()
                , client.getExpireTime()
                , client.getRefreshExpireTime());
        if (!Objects.isNull(result.getRight())) {
            tokenCache.saveRefreshToken(client.getClientId(), request.getUserId(), result.getRight().getRefreshToken());
        }
        return Triple.of(result.getLeft(), result.getMiddle(), Objects.isNull(result.getRight()) ? null : result.getRight().getAccessToken());
    }

    /**
     * @param request: Revoke token request
     * @return: Pair: code(L), message(R)
     */
    public Pair<String, String> revokeToken(RevokeTokenRequest request) {
        TokenClientConfig client = tokenConfig.getClients().get(request.getClientId());
        if (Common.invalidSubClient(request, client)) {
            return Pair.of(CLIENT_INVALID.getCode(), CLIENT_INVALID.getMessage());
        }
        Long result = tokenCache.deleteRefreshToken(client.getClientId(), request.getUserId());
        if (1 > result) {
            return Pair.of(REFRESH_TOKEN_INVALID.getCode(), REFRESH_TOKEN_INVALID.getMessage());
        }
        return Pair.of(SUCCESS.getCode(), SUCCESS.getMessage());
    }
}
