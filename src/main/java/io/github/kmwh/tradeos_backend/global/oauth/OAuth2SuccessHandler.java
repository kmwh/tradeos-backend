package io.github.kmwh.tradeos_backend.global.oauth;

import io.github.kmwh.tradeos_backend.global.jwt.JwtProvider;
import io.github.kmwh.tradeos_backend.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtProvider jwtProvider;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
    User user = oAuth2User.getUser();

    String accessToken = jwtProvider.generateAccessToken(user.getId());
    String refreshToken = jwtProvider.generateRefreshToken(user.getId());

    // Refresh Token 쿠키에 저장
    Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
    response.addCookie(refreshCookie);

    // 프론트엔드 주소로 리다이렉트
    String targetUrl = "http://localhost:3000/oauth2/redirect?token=" + accessToken;
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}
