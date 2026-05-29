package io.github.kmwh.tradeos_backend.quant.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.quant.entity.HmmHistory;

public record HmmChartDto(LocalDateTime timestamp, Integer trendScore) {
  public HmmChartDto(HmmHistory hmmHistory) {
    this(hmmHistory.getTimestamp(), hmmHistory.getTrendScore());
  }
}
