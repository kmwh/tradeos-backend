package io.github.kmwh.tradeos_backend.user.service;

import io.github.kmwh.tradeos_backend.user.dto.UserInfoResponseDto;
import io.github.kmwh.tradeos_backend.user.dto.UserUpdateSettingsRequestDto;
import io.github.kmwh.tradeos_backend.user.entity.User;
import io.github.kmwh.tradeos_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  public UserInfoResponseDto getMyInfo(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    return new UserInfoResponseDto(user);
  }

  @Transactional
  public UserInfoResponseDto updateSettings(Long userId, UserUpdateSettingsRequestDto request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    if (request.reportBatchSize() != null) {
      if (request.reportBatchSize() < 5) {
        throw new IllegalArgumentException("리포트 발행 최소 기준은 5개입니다.");
      }
      user.updateReportBatchSize(request.reportBatchSize());
    }

    return new UserInfoResponseDto(user);
  }
}
