package io.github.kmwh.tradeos_backend.dashboard.dto;

public record DashboardMetricsResponseDto(Double totalRoi, Double winRate,
    Integer currentHmmScore) {
}
