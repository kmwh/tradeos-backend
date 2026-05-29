package io.github.kmwh.tradeos_backend.dashboard.service;

import io.github.kmwh.tradeos_backend.dashboard.dto.DashboardMetricsResponseDto;
import io.github.kmwh.tradeos_backend.dashboard.dto.PnlChartResponseDto;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.repository.JournalRepository;
import io.github.kmwh.tradeos_backend.quant.dto.HmmChartDto;
import io.github.kmwh.tradeos_backend.quant.entity.HmmHistory;
import io.github.kmwh.tradeos_backend.quant.repository.HmmHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
  private final JournalRepository journalRepository;
  private final HmmHistoryRepository hmmHistoryRepository;

  public DashboardMetricsResponseDto getDashboardMetrics(Long userId) {
    List<Journal> journals = journalRepository.findAllByUserIdOrderByEntryTimeDesc(userId);

    double totalRoi = 0.0;
    int winCount = 0;
    int totalTrades = journals.size();

    for (Journal journal : journals) {
      if (journal.getRoi() != null) {
        totalRoi += journal.getRoi(); // 누적 수익률 합산
        if (journal.getRoi() > 0) {
          winCount++; // 수익이 0보다 크면 승리로 카운트
        }
      }
    }

    double winRate =
        (totalTrades == 0) ? 0.0 : Math.round(((double) winCount / totalTrades) * 1000) / 10.0;

    // 가장 최근 HMM 점수 조회
    int currentHmmScore = hmmHistoryRepository
        .findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "timestamp"))).stream()
        .findFirst().map(HmmHistory::getTrendScore).orElse(50); // 데이터 없으면 중립(50)

    return new DashboardMetricsResponseDto(totalRoi, winRate, currentHmmScore);
  }

  // 수익금/수익률 차트 데이터 (최근 30개)
  public List<PnlChartResponseDto> getPnlChartData(Long userId) {
    List<Journal> journals = journalRepository.findAllByUserIdOrderByEntryTimeDesc(userId);

    return journals.stream().limit(30) // 차트 가독성을 위해 최근 30개만 반환
        .map(PnlChartResponseDto::new).collect(Collectors.toList());
  }

  // 추세 점수 차트 데이터 (최근 30개)
  public List<HmmChartDto> getHmmChartData() {
    return hmmHistoryRepository
        .findAll(PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "timestamp"))).stream()
        .map(HmmChartDto::new).collect(Collectors.toList());
  }
}
