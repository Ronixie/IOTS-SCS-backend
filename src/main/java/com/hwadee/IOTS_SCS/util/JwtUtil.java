package com.hwadee.IOTS_SCS.util;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.csu.utils.TokenTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * @author qiershi
 * @version V1.0
 * Copyright (c) 2025, qiershi2006@h163.com All Rights Reserved.
 * @ProjectName: IOTS-SCS-backend
 * @Title: JwtUtil
 * @Package: com.hwadee.IOTS_SCS.util
 * @Description: Jwt工具类
 * @date 2025/7/1 10:59
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private final TokenTools tokenTools;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(String uid) {
        /*return Jwts.builder()
                .setSubject(uid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret) // 旧版直接传 secret
                .compact();*/
        return tokenTools.createShortToken(Long.parseLong(uid));
    }

    public String getUidFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

/*        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();*/
        return String.valueOf(tokenTools.parseShortToken(token));
    }

    public boolean validateToken(String token) {
        try {
            /*Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);*/
            tokenTools.parseShortToken(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}

