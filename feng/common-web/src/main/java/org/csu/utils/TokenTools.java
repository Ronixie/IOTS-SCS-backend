package org.csu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.csu.exception.UnauthorizedException;
import org.csu.properties.TokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * 长/短token工具类
 */
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenProperties.class)
@Component
public class TokenTools {
    private final TokenProperties tokenProperties;
    private static final String DELIMITER = ".";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    /**
     * 创建短token（使用JWT）
     *
     * @param userId 用户信息
     * @return short token
     * */

    public String createShortToken(long userId) {
        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getShortSecretKey().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder().signWith(key)
                .claim("userId", String.valueOf(userId))
                .expiration(new Date(System.currentTimeMillis() + tokenProperties.getShortTokenTTL().toMillis()))
                .compact();

    }

    /**
     * 创建长token（自定义格式）
     *
     * @param userId 用户信息
     * @return long token
     */
    public String createLongToken(String userId) {
        try {
            // 1. 生成token组件
            String timestamp = String.valueOf(System.currentTimeMillis());
            String randomId = UUID.randomUUID().toString();
            String payload = userId + DELIMITER + timestamp + DELIMITER + randomId;

            // 2. 生成签名
            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(
                    tokenProperties.getLongSecretKey().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            hmac.init(secretKey);
            byte[] signature = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            // 3. 组合token
            String encodedPayload = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            String encodedSignature = Base64.getEncoder().encodeToString(signature);
            return encodedPayload + DELIMITER + encodedSignature;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new UnauthorizedException("long token generate fail");
        }
    }

    /**
     * 验证长token
     *
     * @param token 长token
     * @return 用户ID
     */
    public String validateLongToken(String token) {
        try {
            // 1. 分离payload和签名
            String[] parts = token.split("\\.");
            if (parts.length != 2) {
                throw new UnauthorizedException("Invalid token format");
            }

            // 2. 解码payload和签名
            String encodedPayload = parts[0];
            String encodedSignature = parts[1];
            String payload = new String(Base64.getDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            byte[] originalSignature = Base64.getDecoder().decode(encodedSignature);

            // 3. 验证签名
            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(
                    tokenProperties.getLongSecretKey().getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            hmac.init(secretKey);
            byte[] calculatedSignature = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            if (!java.security.MessageDigest.isEqual(originalSignature, calculatedSignature)) {
                throw new UnauthorizedException("Invalid token signature");
            }

            // 4. 解析payload
            String[] payloadParts = payload.split("\\.");
            if (payloadParts.length != 3) {
                throw new UnauthorizedException("Invalid token payload");
            }

            // 5. 验证时间戳
            long timestamp = Long.parseLong(payloadParts[1]);
            if (System.currentTimeMillis() - timestamp > tokenProperties.getLongTokenTTL().toMillis()) {
                throw new UnauthorizedException("Token has expired");
            }

            return payloadParts[0];
        } catch (IllegalArgumentException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new UnauthorizedException("Token validation failed");
        }
    }

    /**
     * 解析短token（JWT）
     *
     * @param token token
     * @return 解析token得到的用户信息
     */
    public long parseShortToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(tokenProperties.getShortSecretKey().getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            System.out.println(claims);
            return Long.parseLong(claims.get("userId").toString());
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired");
        } catch (Exception e) {
            throw new UnauthorizedException("Token validation failed");
        }
    }

}