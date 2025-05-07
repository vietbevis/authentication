package com.vietbevis.authentication.security;

import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.vietbevis.authentication.common.TokenType;
import com.vietbevis.authentication.service.KeyManagerService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final KeyManagerService keyManagerService;

    @Value("${jwt.expiration:86400000}")
    private Duration jwtExpirationInMs;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private Duration refreshTokenExpirationMs;

    private String generateToken(String email, Map<String, Object> claims, Date expiration,
        PrivateKey key) {
        return Jwts.builder()
            .claims(claims)
            .subject(email)
            .issuedAt(new Date())
            .expiration(expiration)
            .signWith(key)
            .compact();
    }

    public String generateAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userPrincipal.getId());

        return generateToken(userPrincipal.getEmail(), claims,
            Date.from(Instant.now().plus(jwtExpirationInMs)),
            keyManagerService.getPrivateKeyAccess());
    }

    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal.getEmail(), new HashMap<>(),
            Date.from(Instant.now().plus(refreshTokenExpirationMs)),
            keyManagerService.getPrivateKeyRefresh());
    }

    public Claims parseToken(String token, TokenType tokenType) {
        try {
            return Jwts
                .parser()
                .verifyWith(tokenType == TokenType.ACCESS_TOKEN
                    ? keyManagerService.getPublicKeyAccess()
                    : keyManagerService.getPublicKeyRefresh())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (MalformedJwtException ex) {
            log.error("Token JWT không đúng định dạng: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT đã hết hạn: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT không được hỗ trợ: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.error("Chữ ký token JWT không hợp lệ: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.error("Lỗi bảo mật khi giải mã token JWT: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Chuỗi token JWT rỗng hoặc không hợp lệ: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Lỗi không xác định khi phân tích token JWT: {}", ex.getMessage());
        }
        return null;
    }
}
