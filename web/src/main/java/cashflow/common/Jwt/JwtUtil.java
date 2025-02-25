package cashflow.common.Jwt;

import cashflow.enums.MessageEnums;
import cashflow.exception.TokenVerificationException;
import cashflow.model.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

/**
 * Jwt工具类，生成JWT和认证
 *
 * @author Cail Gainey
 * @since 2025/1/25 14:58
 **/
@Slf4j
public class JwtUtil {
    private static final String SECRET_KEY = "my_secret";
    private static final long EXPIRATION_TIME_SECONDS = 3 * 60 * 60L; // 3小时
    private static final long REMEMBER_ME_EXPIRATION_TIME_SECONDS = 72 * 60 * 60L; // 3天
    private static final String ALGORITHM = "HS256";
    private static final String TOKEN_TYPE = "JWT";

    /**
     * 生成用户token,设置token超时时间
     */
    public static String createToken(@NonNull User user, boolean rememberMe) {
        long expirationTime = rememberMe ? REMEMBER_ME_EXPIRATION_TIME_SECONDS : EXPIRATION_TIME_SECONDS;
        Date expireDate = new Date(System.currentTimeMillis() + expirationTime * 1000);

        Map<String, Object> headerMap = Map.of("alg", ALGORITHM, "typ", TOKEN_TYPE);

        return JWT.create()
                .withHeader(headerMap)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * 校验token并解析token
     */
    public static Map<String, Claim> verifyToken(@NonNull String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
            DecodedJWT decodedJwt = verifier.verify(token);
            return decodedJwt.getClaims();
        } catch (Exception e) {
            log.error("token解析失败：{}", e.getMessage());
            throw new TokenVerificationException(MessageEnums.TOKEN_INVALID_ERROR.getMessage());
        }
    }

    /**
     * 解析token的过期时间
     */
    public static Date getTokenExpiration(@NonNull String token) {
        try {
            DecodedJWT decodedJwt = JWT.decode(token);
            return decodedJwt.getExpiresAt();
        } catch (Exception e) {
            log.error("获取token过期时间失败：{}", e.getMessage());
            throw new TokenVerificationException(MessageEnums.TOKEN_INVALID_ERROR.getMessage());
        }
    }
}