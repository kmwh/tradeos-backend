package io.github.kmwh.tradeos_backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tendency_reports")
public class TendencyReport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 20)
  private String period; // "WEEKLY", "MONTHLY"

  @Column(nullable = false)
  private Double winRate;

  @Column(length = 50)
  private String frequentEmotion;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String aiSummary;

  @Column(updatable = false)
  private LocalDateTime generatedAt;

  @Builder
  public TendencyReport(User user, String period, Double winRate, String frequentEmotion,
      String aiSummary) {
    this.user = user;
    this.period = period;
    this.winRate = winRate;
    this.frequentEmotion = frequentEmotion;
    this.aiSummary = aiSummary;
    this.generatedAt = LocalDateTime.now();
  }
}
