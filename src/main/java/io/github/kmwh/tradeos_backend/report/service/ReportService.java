package io.github.kmwh.tradeos_backend.report.service;

import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.entity.enums.EmotionTag;
import io.github.kmwh.tradeos_backend.journal.repository.JournalRepository;
import io.github.kmwh.tradeos_backend.report.dto.ReportResponseDto;
import io.github.kmwh.tradeos_backend.report.entity.Report;
import io.github.kmwh.tradeos_backend.report.repository.ReportRepository;
import io.github.kmwh.tradeos_backend.user.entity.User;
import io.github.kmwh.tradeos_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ReportService {

  private final ReportRepository reportRepository;
  private final JournalRepository journalRepository;
  private final UserRepository userRepository;
  private final RestClient restClient;

  @Value("${external.ai.report-url}")
  private String fastApiReportUrl;

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void generatePerformanceReportAsync(Long userId) {
    try {
      log.info("비동기 AI 리포트 자동 발행 시작 (UserId: {})", userId);
      generatePerformanceReport(userId);
      log.info("비동기 AI 리포트 자동 발행 성공 (UserId: {})", userId);
    } catch (Exception e) {
      log.error("비동기 AI 리포트 자동 발행 중 오류 발생 (UserId: {})", userId, e);
    }
  }

  public ReportResponseDto generatePerformanceReport(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    int batchSize = user.getReportBatchSize();
    List<Journal> unreported =
        journalRepository.findByUserIdAndIsReportedFalseOrderByEntryTimeAsc(userId);

    if (unreported.size() < batchSize) {
      throw new IllegalStateException(
          "리포트를 발행하기 위한 일지가 부족합니다. (현재: " + unreported.size() + " / 필요: " + batchSize + ")");
    }

    // 이번에 평가할 데이터 추출
    List<Journal> targetBatch = unreported.subList(0, batchSize);

    // 과거 성과 계산
    long histTotal = journalRepository.countHistoricalTotal(userId);
    double histWinRate = 0.0;
    double histAvgPnl = 0.0;

    if (histTotal > 0) {
      long histWins = journalRepository.countHistoricalWins(userId);
      histWinRate = Math.round(((double) histWins / histTotal) * 1000) / 10.0;

      Double avgPnl = journalRepository.getHistoricalAvgPnl(userId);
      histAvgPnl = avgPnl != null ? Math.round(avgPnl * 100) / 100.0 : 0.0;
    }

    // 이번 주기 성과 계산
    long batchWins = targetBatch.stream().filter(j -> j.getRealizedPnl() > 0).count();
    double batchWinRate = Math.round(((double) batchWins / batchSize) * 1000) / 10.0;
    double batchAvgPnl = Math.round(
        targetBatch.stream().mapToDouble(Journal::getRealizedPnl).average().orElse(0.0) * 100)
        / 100.0;

    // 자주 느낀 감정 추출
    EmotionTag frequentTag =
        targetBatch.stream().map(Journal::getEmotionTag).filter(tag -> tag != null)
            .collect(Collectors.groupingBy(tag -> tag, Collectors.counting())).entrySet().stream()
            .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    String frequentEmotion = frequentTag != null ? frequentTag.name() : "UNKNOWN";

    // 파이썬 평가를 위한 리스트 매핑
    List<Map<String, Object>> journalDataList = targetBatch.stream().map(j -> {
      Map<String, Object> data = new HashMap<>();
      data.put("ticker", j.getTicker() != null ? j.getTicker() : "UNKNOWN");
      data.put("position", j.getPosition() != null ? j.getPosition().name() : "UNKNOWN");
      data.put("leverage", j.getLeverage() != null ? j.getLeverage() : 1.0);
      data.put("entry_reason", j.getEntryReason() != null ? j.getEntryReason() : "기록 없음");
      data.put("exit_reason", j.getExitReason() != null ? j.getExitReason() : "기록 없음");
      data.put("emotion", j.getEmotionTag() != null ? j.getEmotionTag().name() : "UNKNOWN");
      data.put("pnl", j.getRealizedPnl() != null ? j.getRealizedPnl() : 0.0);
      data.put("roi", j.getRoi() != null ? j.getRoi() : 0.0);
      data.put("duration_seconds", j.getDurationSeconds() != null ? j.getDurationSeconds() : 0);
      data.put("hmm_score", j.getMarketHmmScore() != null ? j.getMarketHmmScore() : 50);
      return data;
    }).collect(Collectors.toList());

    Map<String, Object> requestPayload = Map.of("batch_size", batchSize, "historical_win_rate",
        histWinRate, "historical_avg_pnl", histAvgPnl, "batch_win_rate", batchWinRate,
        "batch_avg_pnl", batchAvgPnl, "journals", journalDataList);

    try {
      Map<String, Object> response =
          restClient.post().uri(fastApiReportUrl).contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON).body(requestPayload).retrieve()
              .body(new ParameterizedTypeReference<Map<String, Object>>() {});

      String aiSummary =
          response != null ? (String) response.get("summary_text") : "평가 결과를 받을 수 없습니다.";

      Report report = Report.builder().user(user).batchSize(batchSize)
          .historicalWinRate(histWinRate).historicalAvgPnl(histAvgPnl).batchWinRate(batchWinRate)
          .batchAvgPnl(batchAvgPnl).frequentEmotion(frequentEmotion).aiSummary(aiSummary).build();

      reportRepository.save(report);

      targetBatch.forEach(Journal::markAsReported);
      journalRepository.saveAll(targetBatch);

      return new ReportResponseDto(report);

    } catch (Exception e) {
      log.error("AI 종합 리포트 서버 통신 실패", e);
      throw new RuntimeException("AI 서버 통신 실패: 파이썬 서버가 켜져있는지 확인하세요.");
    }
  }

  @Transactional(readOnly = true)
  public ReportResponseDto getLatestReport(Long userId) {
    Report report = reportRepository.findTopByUserIdOrderByGeneratedAtDesc(userId)
        .orElseThrow(() -> new IllegalArgumentException("발행된 리포트가 없습니다."));
    return new ReportResponseDto(report);
  }

  @Transactional(readOnly = true)
  public Page<ReportResponseDto> getReportHistory(Long userId, Pageable pageable) {
    return reportRepository.findAllByUserIdOrderByGeneratedAtDesc(userId, pageable)
        .map(ReportResponseDto::new);
  }
}
