package io.github.kmwh.tradeos_backend.report.service;

import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.repository.JournalRepository;
import io.github.kmwh.tradeos_backend.report.dto.TendencyReportResponseDto;
import io.github.kmwh.tradeos_backend.report.entity.TendencyReport;
import io.github.kmwh.tradeos_backend.report.repository.TendencyReportRepository;
import io.github.kmwh.tradeos_backend.user.entity.User;
import io.github.kmwh.tradeos_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TendencyReportService {

  private final TendencyReportRepository tendencyReportRepository;
  private final JournalRepository journalRepository;
  private final UserRepository userRepository;
  private final RestClient restClient;

  private static final String FASTAPI_TENDENCY_URL = "http://localhost:8000/api/v1/ai/tendency";

  @Transactional
  public TendencyReportResponseDto generateTendencyReport(Long userId, String period) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    List<Journal> journals = journalRepository.findAllByUserIdOrderByEntryTimeDesc(userId);

    if (journals.isEmpty()) {
      throw new IllegalStateException("매매 일지 데이터가 부족하여 리포트를 생성할 수 없습니다.");
    }

    int winCount = 0;
    for (Journal j : journals) {
      if (j.getRoi() != null && j.getRoi() > 0)
        winCount++;
    }
    double winRate = Math.round(((double) winCount / journals.size()) * 1000) / 10.0;

    String frequentEmotion =
        journals.stream().map(Journal::getEmotionTag).filter(tag -> tag != null && !tag.isBlank())
            .collect(Collectors.groupingBy(tag -> tag, Collectors.counting())).entrySet().stream()
            .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("안정");

    // 파이썬 LangGraph로 보낼 데이터 조립
    Map<String, Object> requestPayload = Map.of("period", period, "win_rate", winRate,
        "frequent_emotion", frequentEmotion, "total_trades", journals.size());

    try {
      // FastAPI 호출하여 종합 평가 받아오기
      Map<String, String> response = restClient.post().uri(FASTAPI_TENDENCY_URL)
          .body(requestPayload).retrieve().body(Map.class);

      String aiSummary = response.get("summary_text");

      // DB에 리포트 저장
      TendencyReport report = TendencyReport.builder().user(user).period(period.toUpperCase())
          .winRate(winRate).frequentEmotion(frequentEmotion).aiSummary(aiSummary).build();

      tendencyReportRepository.save(report);
      return new TendencyReportResponseDto(report);

    } catch (Exception e) {
      log.error("AI 종합 리포트 서버 통신 실패", e);
      throw new RuntimeException("경향 분석 중 오류가 발생했습니다.");
    }
  }

  public TendencyReportResponseDto getLatestTendencyReport(Long userId, String period) {
    TendencyReport report = tendencyReportRepository
        .findTopByUserIdAndPeriodOrderByGeneratedAtDesc(userId, period.toUpperCase())
        .orElseThrow(() -> new IllegalArgumentException("생성된 " + period + " 리포트가 없습니다."));

    return new TendencyReportResponseDto(report);
  }

  public List<TendencyReportResponseDto> getTendencyReportHistory(Long userId) {
    return tendencyReportRepository.findAllByUserIdOrderByGeneratedAtDesc(userId).stream()
        .map(TendencyReportResponseDto::new).collect(Collectors.toList());
  }
}
