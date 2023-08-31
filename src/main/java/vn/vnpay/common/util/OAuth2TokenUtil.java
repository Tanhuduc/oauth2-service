package vn.vnpay.common.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import vn.vnpay.bean.OAuthToken;
import vn.vnpay.bean.dto.UserDTO;
import vn.vnpay.bean.entity.Scope;
import vn.vnpay.core.common.ObjectMapperCommon;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: DucTN
 * Created: 11/08/2023
 **/
@Slf4j
public class OAuth2TokenUtil {
    private static final ObjectMapper MAPPER = ObjectMapperCommon.getInstance();
    private static final String TOKEN_TYPE = "tokenType";
    private static final String SIGNATURE = "signature";
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String PREFIX_TOKEN = "Bearer ";

    private OAuth2TokenUtil() {
    }

    /**
     * @param issuer:           path url of auth-service
     * @param secretKey:        key to gen token
     * @param tokenType:        type of token
     * @param expirationTimeIn: time to accessToken expire
     * @param userDTO:          user info
     * @return : OAuthToken
     */
    public static OAuthToken generateToken(
            String issuer, String secretKey, String tokenType,
            Long expirationTimeIn, UserDTO userDTO) {
        log.info("[generateToken] Start generate token: {}, userName: {}", tokenType, userDTO.getUseName());
        Date issueAt = new Date();
        Date expirationAt = new Date(issueAt.getTime() / 1000 * 1000 + expirationTimeIn); //lam tron den don vi la giay
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        try {
            String token = Jwts.builder()
                    .setIssuer(issuer)
                    .claim(String.valueOf(userDTO.getId()), userDTO)
                    .claim(TOKEN_TYPE, tokenType)
                    .setIssuedAt(issueAt)
                    .setExpiration(expirationAt)
                    .claim(SIGNATURE, generateSignature(userDTO, expirationAt.getTime(), secretKey))
                    .signWith(key)
                    .compact();
            log.info("[generateToken] Generate token success");
            return OAuthToken.builder()
                    .token(new StringBuilder(PREFIX_TOKEN).append(token).toString())
                    .expireTime(expirationAt)
                    .build();
        } catch (Exception e) {
            log.info("[generateToken] Has error");
            log.error("[generateToken] Exception: ", e);
            throw new RuntimeException(e);
        }
    }

    private static String generateSignature(UserDTO userDTO, Long expirationTime, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String message = buildMessage(userDTO.getId(), userDTO.getRole(), userDTO.getUseName(), userDTO.getLstScope(), expirationTime);
        log.info("Message: {}", message);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(secretKeySpec);
        byte[] signatureByes = mac.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(signatureByes);
    }

    private static String buildMessage(Integer id, String role, String useName, List<Scope> lstScope, Long expirationTime) {
        return new StringBuilder(SIGNATURE)
                .append("|").append(id)
                .append("|").append(role)
                .append("|").append(useName)
                .append("|").append(convertToString(lstScope))
                .append("|").append(expirationTime)
                .toString();
    }

    private static String convertToString(List<Scope> lstScope) {
        return lstScope.stream().map(Scope::getScope).collect(Collectors.joining("|"));
    }

    /**
     * @param authorization: token need validate
     * @param clientId:      id of client
     * @param userId:        id of user
     * @param secretKey:     key use to validate
     * @return : result validate
     */
    public static boolean invalidateToken(String authorization, String clientId, String userId, String secretKey, String tokenType) {
        log.info("[invalidateToken] Start validate token type: {}", tokenType);
        try {
            if (StringUtils.isBlank(authorization) || !authorization.startsWith(PREFIX_TOKEN)) {
                log.info("[invalidateToken] Authorization is wrong format");
                return true;
            }
            Claims claims = getClaims(authorization, secretKey);
            if (!tokenType.equals(claims.get(TOKEN_TYPE).toString())) {
                log.info("[invalidateToken] Token type not access token");
                return true;
            }
            Map<String, Object> mUserDTO = claims.get(userId, Map.class);
            if (Objects.isNull(mUserDTO) || mUserDTO.isEmpty()) {
                log.info("[invalidateToken] Token is error, not found user info");
                return true;
            }
            UserDTO userDTO = MAPPER.convertValue(mUserDTO, UserDTO.class);
            List<String> lstScope = userDTO.getLstScope().stream()
                    .map(Scope::getScope).toList();
            if (!lstScope.contains(clientId)) {
                log.info("[invalidateToken] UserId: {} doesn't have scope in clientId: {}", userId, clientId);
                return true;
            }
            String signature = claims.get(SIGNATURE, String.class);
            if (StringUtils.isBlank(signature)) {
                log.info("[invalidateToken] Token is error, not found signature");
                return true;
            }
            return !verifySignature(signature, userDTO, claims.getExpiration().getTime(), secretKey);
        } catch (Exception e) {
            log.info("[invalidateToken] Has error");
            log.info("[invalidateToken] Validate {} error", tokenType);
            if (e instanceof ExpiredJwtException) {
                log.error("[invalidateToken] Validate is expired, Exception: ", e);
                return true;
            }
            log.error("[invalidateToken] Validate is error, Exception: ", e);
            return true;
        }
    }

    public static Claims getClaims(String authorization, String secretKey) {
        String token = authorization.substring(PREFIX_TOKEN.length());
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return claimsJws.getBody();
    }

    private static boolean verifySignature(String signature, UserDTO userDTO, Long expirationTime, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        String signatureExpect = generateSignature(userDTO, expirationTime, secretKey);
        if (!signature.equals(signatureExpect)) {
            log.info("[verifySignature] Signature is wrong");
            return false;
        }
        log.info("[verifySignature] verify success");
        return true;
    }

    public static String generateAuthorizeCode(Integer userId) {
        log.info("[generateAuthorizeCode] Start generate");
        String code = new StringBuilder(userId).append("_").append(NanoIdUtils.randomNanoId()).toString();
        String authorizationCode = Base64.getEncoder().encodeToString(code.getBytes());
        log.info("[generateAuthorizeCode] Finish generate");
        return authorizationCode;
    }


}
