package io.github.kmwh.tradeos_backend.report.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.report.entity.TendencyReport;

public record TendencyReportResponseDto(Long tendencyId, String period, Double winRate,
    String frequentEmotion, String aiSummary, LocalDateTime generatedAt) {
  public TendencyReportResponseDto(TendencyReport report) {
    this(report.getId(), report.getPeriod(), report.getWinRate(), report.getFrequentEmotion(),
        report.getAiSummary(), report.getGeneratedAt());
  }
}
