package io.github.kmwh.tradeos_backend.dashboard.service;

import io.github.kmwh.tradeos_backend.dashboard.dto.DashboardMetricsResponseDto;
import io.github.kmwh.tradeos_backend.dashboard.dto.PnlChartResponseDto;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.repository.JournalRepository;
import io.github.kmwh.tradeos_backend.quant.dto.HmmChartDto;
import io.github.kmwh.tradeos_backend.quant.repository.HmmHistoryRepository;
import io.github.kmwh.tradeos_backend.user.entity.User;
import io.github.kmwh.tradeos_backend.user.repository.UserRepository;
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
  private final UserRepository userRepository;
  private final HmmHistoryRepository hmmHistoryRepository;

  public DashboardMetricsResponseDto getDashboardMetrics(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();
    List<Journal> journals = journalRepository.findAllByUserIdOrderByEntryTimeDesc(userId);

    int totalTrades = journals.size();
    if (totalTrades == 0) {
      return new DashboardMetricsResponseDto(0, "데이터 부족", "데이터 부족", "데이터 부족", 0.0, 0.0, 0.0);
    }

    long daysActive = Duration.between(user.getCreatedAt(), LocalDateTime.now()).toDays();
    daysActive = daysActive == 0 ? 1 : daysActive;
    double freq = (double) totalTrades / daysActive;
    String tradeFrequency = freq >= 3.0 ? "HIGH" : freq >= 1.0 ? "MEDIUM" : "LOW";

    double avgLeverage = journals.stream().mapToDouble(Journal::getLeverage).average().orElse(1.0);
    String riskTolerance = avgLeverage >= 20.0 ? "HIGH" : avgLeverage >= 5.0 ? "MEDIUM" : "LOW";

    double avgDuration =
        journals.stream().mapToDouble(Journal::getDurationSeconds).average().orElse(0.0);
    String tradeDuration = avgDuration >= 86400 ? "LONG-TERM" : "SHORT-TERM";

    long winCount = journals.stream().filter(j -> j.getRealizedPnl() > 0).count();
    double totalWinRate = Math.round(((double) winCount / totalTrades) * 1000) / 10.0;

    List<Journal> trendJournals = journals.stream()
        .filter(j -> j.getMarketHmmScore() != null && j.getMarketHmmScore() > 60).toList();
    double trendWinRate = 0.0;
    if (!trendJournals.isEmpty()) {
      long trendWins = trendJournals.stream().filter(j -> j.getRealizedPnl() > 0).count();
      trendWinRate = Math.round(((double) trendWins / trendJournals.size()) * 1000) / 10.0;
    }

    List<Journal> nonTrendJournals = journals.stream()
        .filter(j -> j.getMarketHmmScore() != null && j.getMarketHmmScore() <= 60).toList();
    double nonTrendWinRate = 0.0;
    if (!nonTrendJournals.isEmpty()) {
      long nonTrendWins = nonTrendJournals.stream().filter(j -> j.getRealizedPnl() > 0).count();
      nonTrendWinRate = Math.round(((double) nonTrendWins / nonTrendJournals.size()) * 1000) / 10.0;
    }

    return new DashboardMetricsResponseDto(totalTrades, tradeFrequency, riskTolerance,
        tradeDuration, totalWinRate, trendWinRate, nonTrendWinRate);
  }

  public List<PnlChartResponseDto> getPnlChartData(Long userId) {
    return journalRepository.findAllByUserIdOrderByEntryTimeDesc(userId).stream().limit(50)
        .map(PnlChartResponseDto::new).collect(Collectors.toList());
  }

  public List<HmmChartDto> getHmmChartData() {
    return hmmHistoryRepository
        .findAll(PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "timestamp"))).stream()
        .map(HmmChartDto::new).collect(Collectors.toList());
  }
}
