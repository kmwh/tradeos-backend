package io.github.kmwh.tradeos_backend.journal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Duration;
import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.user.entity.User;
import io.github.kmwh.tradeos_backend.journal.entity.enums.Position;
import io.github.kmwh.tradeos_backend.journal.entity.enums.EmotionTag;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "journals")
public class Journal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, length = 20)
  private String ticker;

  @Column(nullable = false)
  private LocalDateTime entryTime;

  @Column(nullable = false)
  private LocalDateTime exitTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Position position;

  @Column(nullable = false)
  private Double entryPrice;

  @Column(nullable = false)
  private Double exitPrice;

  @Min(1)
  @Column(columnDefinition = "DOUBLE DEFAULT 1.0", nullable = false)
  private Double leverage = 1.0;

  @Column(nullable = false)
  private Double volume;

  @Column(nullable = false)
  private Double fee;

  private Double realizedPnl;
  private Double roi;

  @Column(columnDefinition = "TEXT")
  private String entryReason;

  @Column(columnDefinition = "TEXT")
  private String exitReason;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private EmotionTag emotionTag;

  @Column(nullable = false)
  private Long durationSeconds;

  @Column(nullable = true)
  private Integer marketHmmScore;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private Boolean isReported = false;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Builder
  public Journal(User user, String ticker, LocalDateTime entryTime, LocalDateTime exitTime,
      Position position, Double entryPrice, Double exitPrice, Double leverage, Double volume,
      Double fee, String entryReason, String exitReason, EmotionTag emotionTag) {
    this.user = user;
    this.ticker = ticker;
    this.entryTime = entryTime;
    this.exitTime = exitTime;
    this.position = position;
    this.entryPrice = entryPrice;
    this.exitPrice = exitPrice;
    this.leverage = leverage != null ? leverage : 1.0;
    this.volume = volume;
    this.fee = fee != null ? fee : 0.0;
    this.entryReason = entryReason;
    this.exitReason = exitReason;
    this.emotionTag = emotionTag;
    this.createdAt = LocalDateTime.now();
  }

  public void updateJournal(String ticker, LocalDateTime entryTime, LocalDateTime exitTime,
      Position position, Double entryPrice, Double exitPrice, Double leverage, Double volume,
      Double fee, String entryReason, String exitReason, EmotionTag emotionTag) {
    this.ticker = ticker;
    this.entryTime = entryTime;
    this.exitTime = exitTime;
    this.position = position;
    this.entryPrice = entryPrice;
    this.exitPrice = exitPrice;
    this.leverage = leverage;
    this.volume = volume;
    this.fee = fee;
    this.entryReason = entryReason;
    this.exitReason = exitReason;
    this.emotionTag = emotionTag;
  }

  public void calculateMetrics(Integer hmmScore) {
    double margin = (this.entryPrice * this.volume) / this.leverage;
    double grossPnl = 0.0;
    if (this.position == Position.LONG) {
      grossPnl = (this.exitPrice - this.entryPrice) * this.volume;
    } else if (this.position == Position.SHORT) {
      grossPnl = (this.entryPrice - this.exitPrice) * this.volume;
    }
    this.realizedPnl = grossPnl - this.fee;

    if (margin > 0) {
      double rawRoi = (this.realizedPnl / margin) * 100.0;
      this.roi = Math.round(rawRoi * 100.0) / 100.0;
    } else {
      this.roi = 0.0;
    }

    // 유지 시간 및 HMM 추세 점수 캐싱
    this.durationSeconds = Duration.between(this.entryTime, this.exitTime).getSeconds();
    this.marketHmmScore = hmmScore;
  }

  public void markAsReported() {
    this.isReported = true;
  }
}
