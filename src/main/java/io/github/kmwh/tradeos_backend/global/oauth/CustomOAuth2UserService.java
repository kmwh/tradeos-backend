package io.github.kmwh.tradeos_backend.global.oauth;

import io.github.kmwh.tradeos_backend.user.entity.User;
import io.github.kmwh.tradeos_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String provider = userRequest.getClientRegistration().getRegistrationId();
    String providerId = oAuth2User.getAttribute("sub");
    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");

    // DB에 없으면 신규 가입 처리, 있으면 기존 정보 유지
    User user = userRepository.findByProviderId(providerId)
        .orElseGet(() -> userRepository.save(User.builder().email(email).nickname(name)
            .provider(provider).providerId(providerId).role("ROLE_USER").build()));

    return new CustomOAuth2User(user, oAuth2User.getAttributes());
  }
}
