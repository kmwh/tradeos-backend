package io.github.kmwh.tradeos_backend.quant.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hmm_history")
public class HmmHistory {
  @Id
  @Column(nullable = false)
  private LocalDateTime timestamp;

  @Column(nullable = false, length = 20)
  private String symbol;

  @Column(nullable = false)
  private Integer trendScore;

  @Builder
  public HmmHistory(LocalDateTime timestamp, String symbol, Integer trendScore) {
    this.timestamp = timestamp;
    this.symbol = symbol;
    this.trendScore = trendScore;
  }
}
