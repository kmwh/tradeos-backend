package io.github.kmwh.tradeos_backend.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

  private final SecretKey secretKey;
  private final long accessExpiration;
  private final long refreshExpiration;

  public JwtProvider(@Value("${jwt.secret}") String secret,
      @Value("${jwt.access-expiration}") long accessExpiration,
      @Value("${jwt.refresh-expiration}") long refreshExpiration) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessExpiration = accessExpiration;
    this.refreshExpiration = refreshExpiration;
  }

  // Access Token 생성
  public String generateAccessToken(Long userId) {
    return Jwts.builder().subject(userId.toString()).issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + accessExpiration)).signWith(secretKey)
        .compact();
  }

  // Refresh Token 생성
  public String generateRefreshToken(Long userId) {
    return Jwts.builder().subject(userId.toString()).issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + refreshExpiration)).signWith(secretKey)
        .compact();
  }

  // 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 토큰에서 userId 추출
  public Long getUserIdFromToken(String token) {
    Claims claims =
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    return Long.parseLong(claims.getSubject());
  }
}
