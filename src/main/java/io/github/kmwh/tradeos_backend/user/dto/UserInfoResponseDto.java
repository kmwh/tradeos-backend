package io.github.kmwh.tradeos_backend.user.dto;

import io.github.kmwh.tradeos_backend.user.entity.User;

public record UserInfoResponseDto(Long userId, String email, String nickname) {
  public UserInfoResponseDto(User user) {
    this(user.getId(), user.getEmail(), user.getNickname());
  }
}
