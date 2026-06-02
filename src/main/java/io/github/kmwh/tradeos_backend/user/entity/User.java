package io.github.kmwh.tradeos_backend.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String email;

  @Column(nullable = false, length = 50)
  private String nickname;

  @Column(nullable = false, length = 20)
  private String provider;

  @Column(nullable = false, unique = true)
  private String providerId;

  @Column(nullable = false, length = 20)
  private String role;

  @Column(nullable = false)
  private Integer reportBatchSize = 10;

  @Column(updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Builder
  public User(String email, String nickname, String provider, String providerId, String role) {
    this.email = email;
    this.nickname = nickname;
    this.provider = provider;
    this.providerId = providerId;
    this.role = role;
  }

  public void updateReportBatchSize(Integer batchSize) {
    if (batchSize < 2) {
      throw new IllegalArgumentException("리포트 발행 최소 기준은 2개입니다.");
    }
    this.reportBatchSize = batchSize;
  }
}
