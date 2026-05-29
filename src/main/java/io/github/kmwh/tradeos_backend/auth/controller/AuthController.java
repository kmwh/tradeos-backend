package io.github.kmwh.tradeos_backend.auth.controller;

import io.github.kmwh.tradeos_backend.auth.dto.TokenResponseDto;
import io.github.kmwh.tradeos_backend.auth.service.AuthService;
import io.github.kmwh.tradeos_backend.global.dto.MessageResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponseDto> refreshToken(
      @CookieValue(value = "refreshToken", required = false) String refreshToken) {
    return ResponseEntity.ok(authService.refreshToken(refreshToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<MessageResponseDto> logout(HttpServletResponse response) {
    Cookie cookie = new Cookie("refreshToken", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);

    return ResponseEntity.ok(new MessageResponseDto("로그아웃 되었습니다."));
  }
}
