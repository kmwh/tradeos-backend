package io.github.kmwh.tradeos_backend.auth.service;

import io.github.kmwh.tradeos_backend.auth.dto.TokenResponseDto;
import io.github.kmwh.tradeos_backend.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtProvider jwtProvider;

  public TokenResponseDto refreshToken(String refreshToken) {
    if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다. 다시 로그인해주세요.");
    }

    Long userId = jwtProvider.getUserIdFromToken(refreshToken);
    String newAccessToken = jwtProvider.generateAccessToken(userId);

    return new TokenResponseDto(newAccessToken);
  }
}
