package io.github.kmwh.tradeos_backend.journal.service;

import io.github.kmwh.tradeos_backend.journal.dto.AiFeedbackResponseDto;
import io.github.kmwh.tradeos_backend.journal.dto.JournalDetailResponseDto;
import io.github.kmwh.tradeos_backend.journal.dto.JournalIdResponseDto;
import io.github.kmwh.tradeos_backend.journal.dto.JournalListResponseDto;
import io.github.kmwh.tradeos_backend.journal.dto.JournalRequestDto;
import io.github.kmwh.tradeos_backend.journal.entity.AiFeedback;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.repository.AiFeedbackRepository;
import io.github.kmwh.tradeos_backend.journal.repository.JournalRepository;
import io.github.kmwh.tradeos_backend.quant.entity.HmmHistory;
import io.github.kmwh.tradeos_backend.quant.repository.HmmHistoryRepository;
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
public class JournalService {

  private final JournalRepository journalRepository;
  private final UserRepository userRepository;
  private final HmmHistoryRepository hmmHistoryRepository;
  private final AiFeedbackRepository aiFeedbackRepository;
  private final RestClient restClient;

  private static final String FASTAPI_AI_URL = "http://localhost:8000/api/v1/ai/analyze";

  @Transactional
  public JournalIdResponseDto createJournal(Long userId, JournalRequestDto request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    Journal journal = Journal.builder().user(user).entryTime(request.entryTime())
        .exitTime(request.exitTime()).position(request.position()).entryPrice(request.entryPrice())
        .exitPrice(request.exitPrice()).leverage(request.leverage())
        .realizedPnl(request.realizedPnl()).roi(request.roi()).entryReason(request.entryReason())
        .exitReason(request.exitReason()).emotionTag(request.emotionTag()).build();

    return new JournalIdResponseDto(journalRepository.save(journal).getId());
  }

  public List<JournalListResponseDto> getJournals(Long userId) {
    return journalRepository.findAllByUserIdOrderByEntryTimeDesc(userId).stream()
        .map(JournalListResponseDto::new).collect(Collectors.toList());
  }

  public JournalDetailResponseDto getJournalDetail(Long userId, Long journalId) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);
    return new JournalDetailResponseDto(journal);
  }

  @Transactional
  public void updateJournal(Long userId, Long journalId, JournalRequestDto request) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);

    if (journal.getIsAnalyzed()) {
      throw new IllegalStateException("AI 분석이 완료된 일지는 수정할 수 없습니다. 피드백을 먼저 삭제해주세요.");
    }

    journal.updateJournal(request.entryTime(), request.exitTime(), request.position(),
        request.entryPrice(), request.exitPrice(), request.leverage(), request.realizedPnl(),
        request.roi(), request.entryReason(), request.exitReason(), request.emotionTag());
  }

  @Transactional
  public void deleteJournal(Long userId, Long journalId) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);
    journalRepository.delete(journal);
  }

  @Transactional
  public void deleteAiFeedback(Long userId, Long journalId) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);

    if (journal.getAiFeedback() != null) {
      journal.removeAiFeedback();
    }
  }

  @Transactional
  public AiFeedbackResponseDto analyzeJournal(Long userId, Long journalId) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);

    if (journal.getIsAnalyzed()) {
      throw new IllegalStateException("이미 분석된 일지입니다.");
    }

    int hmmScore = hmmHistoryRepository
        .findTopByTimestampLessThanEqualOrderByTimestampDesc(journal.getEntryTime())
        .map(HmmHistory::getTrendScore).orElse(50);

    Map<String, Object> requestPayload =
        Map.of("position", journal.getPosition(), "entry_reason", journal.getEntryReason(),
            "exit_reason", journal.getExitReason(), "emotion_tag", journal.getEmotionTag(),
            "hmm_score", hmmScore, "roi", journal.getRoi() != null ? journal.getRoi() : 0.0);

    try {
      Map<String, String> response =
          restClient.post().uri(FASTAPI_AI_URL).body(requestPayload).retrieve().body(Map.class);

      String feedbackText = response.get("feedback_text");

      AiFeedback feedback =
          AiFeedback.builder().journal(journal).feedbackText(feedbackText).build();
      aiFeedbackRepository.save(feedback);
      journal.updateIsAnalyzed(true);

      return new AiFeedbackResponseDto(feedbackText);

    } catch (Exception e) {
      log.error("AI 서버 통신 실패", e);
      throw new RuntimeException("AI 분석 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
  }

  private Journal getJournalAndCheckOwnership(Long userId, Long journalId) {
    Journal journal = journalRepository.findById(journalId)
        .orElseThrow(() -> new IllegalArgumentException("해당 매매 일지를 찾을 수 없습니다."));

    if (!journal.getUser().getId().equals(userId)) {
      throw new IllegalArgumentException("본인의 일지만 접근할 수 있습니다.");
    }
    return journal;
  }
}
