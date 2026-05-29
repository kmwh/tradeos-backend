package io.github.kmwh.tradeos_backend.user.controller;

import io.github.kmwh.tradeos_backend.user.dto.UserInfoResponseDto;
import io.github.kmwh.tradeos_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserInfoResponseDto> getMyInfo(@AuthenticationPrincipal Long userId) {
    return ResponseEntity.ok(userService.getMyInfo(userId));
  }
}
