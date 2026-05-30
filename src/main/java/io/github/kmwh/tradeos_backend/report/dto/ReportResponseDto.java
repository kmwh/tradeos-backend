package io.github.kmwh.tradeos_backend.report.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.report.entity.Report;

public record ReportResponseDto(Long tendencyId, Integer batchSize, Double winRate,
    String frequentEmotion, String aiSummary, LocalDateTime generatedAt) {
  public ReportResponseDto(Report report) {
    this(report.getId(), report.getBatchSize(), report.getBatchWinRate(),
        report.getFrequentEmotion(), report.getAiSummary(), report.getGeneratedAt());
  }
}
