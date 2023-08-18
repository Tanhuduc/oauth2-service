package vn.vnpay.usecase;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import vn.vnpay.cache.TokenCache;
import vn.vnpay.common.Common;
import vn.vnpay.common.util.OAuth2TokenUtil;
import vn.vnpay.config.TokenClientConfig;
import vn.vnpay.config.TokenConfig;
import vn.vnpay.dto.UserDTO;
import vn.vnpay.netty.request.GenerateTokenRequest;
import vn.vnpay.netty.request.RefreshTokenRequest;
import vn.vnpay.netty.request.RevokeTokenRequest;
import vn.vnpay.netty.request.VerifyTokenRequest;
import vn.vnpay.netty.response.data.OAuthToken;

import java.util.Objects;

import static vn.vnpay.netty.Error.Error.REFRESH_TOKEN_INVALID;
import static vn.vnpay.netty.Error.Error.SUCCESS;

/**
 * @Author: DucTN
 * Created: 14/08/2023
 **/
public class OAuth2UseCase {
    private final TokenConfig tokenConfig = TokenConfig.getInstance();
    private final TokenCache tokenCache = TokenCache.getINSTANCE();

    private static OAuth2UseCase instance;

    public static OAuth2UseCase getInstance() {
        if (Objects.isNull(instance)) {
            instance = new OAuth2UseCase();
        }
        return instance;
    }

    private OAuth2UseCase() {
    }

    public String generateToken(GenerateTokenRequest request) {
        TokenClientConfig client = tokenConfig.getClients().get(request.getClientId());
        if (Common.invalidSubClient(request, client)) {
            return null;
        }
        //Todo
        if (!"ABC".equals(request.getUserName()) || !"abc".equals(request.getPassword())) {
            return null;
        }
        //Todo draft
        UserDTO userDTO = UserDTO.builder().id("abc").role("USER").useName("ABC").build();
        OAuthToken oAuthToken = OAuth2TokenUtil
                .generateToken(
                        tokenConfig.getIssuer(),
                        tokenConfig.getSecretKey(),
                        client.getClientId(),
                        client.getExpireTime(),
                        client.getRefreshExpireTime(),
                        userDTO);
        if (Objects.isNull(oAuthToken)) {
            return null;
        }
        tokenCache.saveRefreshToken(request.getClientId(), userDTO.getId(), oAuthToken.getRefreshToken());
        return oAuthToken.getAccessToken();
    }

    /**
     * @param request: verify token request
     * @return Triple: message(L), result validate(M), code(R)
     */
    public Triple<String, Boolean, String> verifyToken(VerifyTokenRequest request) {
        TokenClientConfig client = tokenConfig.getClients().get(request.getClientId());
        if (Common.invalidSubClient(request, client)) {
            return null;
        }
        return OAuth2TokenUtil
                .validateAccessToken(request.getAccessToken(), tokenConfig.getSecretKey(), request.getClientId());
    }

    /**
     *
     * @param request: Refresh token request
     * @return: Triple: code(L), message(M), OAuthToken(R)
     */
    public Triple<String, String, String> refreshToken(RefreshTokenRequest request) {
        TokenClientConfig client = tokenConfig.getClients().get(request.getClientId());
        if (Common.invalidSubClient(request, client)) {
            return null;
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
     *
     * @param request: Revoke token request
     * @return: Pair: code(L), message(R)
     */
    public Pair<String, String> revokeToken(RevokeTokenRequest request) {
        TokenClientConfig client = tokenConfig.getClients().get(request.getClientId());
        if (Common.invalidSubClient(request, client)) {
            return null;
        }
        Long result = tokenCache.deleteRefreshToken(client.getClientId(), request.getUserId());
        if (1 > result) {
            return Pair.of(REFRESH_TOKEN_INVALID.getCode(), REFRESH_TOKEN_INVALID.getMessage());
        }
        return Pair.of(SUCCESS.getCode(), SUCCESS.getMessage());
    }
}
