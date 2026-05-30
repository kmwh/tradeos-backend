package io.github.kmwh.tradeos_backend.dashboard.dto;

public record DashboardMetricsResponseDto(Integer totalTrades, String tradeFrequency,
    String riskTolerance, String tradeDuration, Double totalWinRate, Double trendWinRate,
    Double nonTrendWinRate) {
}
