package io.github.kmwh.tradeos_backend.user.controller;

import io.github.kmwh.tradeos_backend.user.dto.UserInfoResponseDto;
import io.github.kmwh.tradeos_backend.user.dto.UserUpdateSettingsRequestDto;
import io.github.kmwh.tradeos_backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<UserInfoResponseDto> getMyInfo(@AuthenticationPrincipal Long userId) {
    return ResponseEntity.ok(userService.getMyInfo(userId));
  }

  @PutMapping("/settings")
  public ResponseEntity<UserInfoResponseDto> updateSettings(@AuthenticationPrincipal Long userId,
      @Valid @RequestBody UserUpdateSettingsRequestDto request) {
    return ResponseEntity.ok(userService.updateSettings(userId, request));
  }
}
