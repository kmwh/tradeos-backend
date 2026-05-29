package io.github.kmwh.tradeos_backend.dashboard.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;

public record PnlChartResponseDto(LocalDateTime exitTime, Double realizedPnl, Double roi) {
  public PnlChartResponseDto(Journal journal) {
    this(journal.getExitTime(), journal.getRealizedPnl(), journal.getRoi());
  }
}
