package io.github.kmwh.tradeos_backend.dashboard.service;

import io.github.kmwh.tradeos_backend.dashboard.dto.DashboardMetricsResponseDto;
import io.github.kmwh.tradeos_backend.dashboard.dto.PnlChartResponseDto;
import io.github.kmwh.tradeos_backend.journal.repository.JournalRepository;
import io.github.kmwh.tradeos_backend.quant.dto.HmmChartDto;
import io.github.kmwh.tradeos_backend.quant.repository.HmmHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
  private final JournalRepository journalRepository;
  private final HmmHistoryRepository hmmHistoryRepository;

  public DashboardMetricsResponseDto getDashboardMetrics(Long userId) {
    long totalTrades = journalRepository.countByUserId(userId);
    if (totalTrades == 0) {
      return new DashboardMetricsResponseDto(0, null, null, null, null, null, null);
    }

    // 거래 빈도
    LocalDateTime earliestTrade = journalRepository.getEarliestTradeTime(userId);
    LocalDateTime latestTrade = journalRepository.getLatestTradeTime(userId);
    long daysActive = Duration.between(earliestTrade, latestTrade).toDays();
    daysActive = daysActive <= 0 ? 1 : daysActive;
    double freq = (double) totalTrades / daysActive;
    String tradeFrequency = freq > 3.0 ? "HIGH" : freq < 1.0 ? "LOW" : "MEDIUM";

    // 평균 레버리지
    Double avgLev = journalRepository.getAverageLeverage(userId);
    double avgLeverage = avgLev != null ? avgLev : 1.0;
    String riskTolerance = avgLeverage >= 20.0 ? "HIGH" : avgLeverage >= 5.0 ? "MEDIUM" : "LOW";

    // 거래 유지 시간 성향
    Double avgDur = journalRepository.getAverageDuration(userId);
    double avgDuration = avgDur != null ? avgDur : 0.0;
    String tradeDuration = avgDuration >= 86400 ? "LONG-TERM" : "SHORT-TERM";

    // 총 승률
    long totalWins = journalRepository.countTotalWins(userId);
    double totalWinRate = Math.round(((double) totalWins / totalTrades) * 1000) / 10.0;

    // 추세장 승률
    long trendTrades = journalRepository.countTrendTrades(userId);
    Double trendWinRate = null;
    if (trendTrades > 0) {
      long trendWins = journalRepository.countTrendWins(userId);
      trendWinRate = Math.round(((double) trendWins / trendTrades) * 1000) / 10.0;
    }

    // 비추세장 승률
    long nonTrendTrades = journalRepository.countNonTrendTrades(userId);
    Double nonTrendWinRate = null;
    if (nonTrendTrades > 0) {
      long nonTrendWins = journalRepository.countNonTrendWins(userId);
      nonTrendWinRate = Math.round(((double) nonTrendWins / nonTrendTrades) * 1000) / 10.0;
    }

    return new DashboardMetricsResponseDto((int) totalTrades, tradeFrequency, riskTolerance,
        tradeDuration, totalWinRate, trendWinRate, nonTrendWinRate);
  }

  public List<PnlChartResponseDto> getPnlChartData(Long userId) {
    return journalRepository.findTop50ByUserIdOrderByEntryTimeDesc(userId).stream()
        .map(PnlChartResponseDto::new).collect(Collectors.toList());
  }

  public List<HmmChartDto> getHmmChartData() {
    return hmmHistoryRepository
        .findAll(PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "timestamp"))).stream()
        .map(HmmChartDto::new).collect(Collectors.toList());
  }
}
