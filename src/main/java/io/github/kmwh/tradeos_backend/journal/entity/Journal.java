package io.github.kmwh.tradeos_backend.journal.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.user.entity.User;

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

  @Column(nullable = false, length = 10)
  private String position;

  @Column(nullable = false)
  private Double entryPrice;

  @Column(nullable = false)
  private Double exitPrice;

  @Column(columnDefinition = "DOUBLE DEFAULT 1.0", nullable = false)
  private Double leverage = 1.0;

  private Double realizedPnl;
  private Double roi;

  @Column(columnDefinition = "TEXT")
  private String entryReason;

  @Column(columnDefinition = "TEXT")
  private String exitReason;

  @Column(length = 50)
  private String emotionTag;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Builder
  public Journal(User user, String ticker, LocalDateTime entryTime, LocalDateTime exitTime,
      String position, Double entryPrice, Double exitPrice, Double leverage, Double realizedPnl,
      Double roi, String entryReason, String exitReason, String emotionTag) {
    this.user = user;
    this.ticker = ticker;
    this.entryTime = entryTime;
    this.exitTime = exitTime;
    this.position = position;
    this.entryPrice = entryPrice;
    this.exitPrice = exitPrice;
    this.leverage = leverage != null ? leverage : 1.0;
    this.realizedPnl = realizedPnl;
    this.roi = roi;
    this.entryReason = entryReason;
    this.exitReason = exitReason;
    this.emotionTag = emotionTag;
    this.createdAt = LocalDateTime.now();
  }

  public void updateJournal(String ticker, LocalDateTime entryTime, LocalDateTime exitTime,
      String position, Double entryPrice, Double exitPrice, Double leverage, Double realizedPnl,
      Double roi, String entryReason, String exitReason, String emotionTag) {
    this.ticker = ticker;
    this.entryTime = entryTime;
    this.exitTime = exitTime;
    this.position = position;
    this.entryPrice = entryPrice;
    this.exitPrice = exitPrice;
    this.leverage = leverage;
    this.realizedPnl = realizedPnl;
    this.roi = roi;
    this.entryReason = entryReason;
    this.exitReason = exitReason;
    this.emotionTag = emotionTag;
  }
}
