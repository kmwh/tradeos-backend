package io.github.kmwh.tradeos_backend.report.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.user.entity.User;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "reports")
public class Report {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Integer batchSize;

  private Double historicalWinRate;
  private Double historicalAvgPnl;
  private Double batchWinRate;
  private Double batchAvgPnl;

  @Column(length = 50)
  private String frequentEmotion;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String aiSummary;

  @Column(updatable = false)
  private LocalDateTime generatedAt;

  @Builder
  public Report(User user, Integer batchSize, Double historicalWinRate, Double historicalAvgPnl,
      Double batchWinRate, Double batchAvgPnl, String frequentEmotion, String aiSummary) {
    this.user = user;
    this.batchSize = batchSize;
    this.historicalWinRate = historicalWinRate;
    this.historicalAvgPnl = historicalAvgPnl;
    this.batchWinRate = batchWinRate;
    this.batchAvgPnl = batchAvgPnl;
    this.frequentEmotion = frequentEmotion;
    this.aiSummary = aiSummary;
    this.generatedAt = LocalDateTime.now();
  }
}
