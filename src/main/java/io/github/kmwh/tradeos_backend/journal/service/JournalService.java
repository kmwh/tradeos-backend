package io.github.kmwh.tradeos_backend.journal.service;

import io.github.kmwh.tradeos_backend.journal.dto.*;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.repository.JournalRepository;
import io.github.kmwh.tradeos_backend.quant.dto.HmmChartDto;
import io.github.kmwh.tradeos_backend.quant.entity.HmmHistory;
import io.github.kmwh.tradeos_backend.quant.repository.HmmHistoryRepository;
import io.github.kmwh.tradeos_backend.report.service.ReportService;
import io.github.kmwh.tradeos_backend.user.entity.User;
import io.github.kmwh.tradeos_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JournalService {

  private final JournalRepository journalRepository;
  private final UserRepository userRepository;
  private final HmmHistoryRepository hmmHistoryRepository;

  @Lazy
  private final ReportService reportService;

  @Transactional
  public JournalIdResponseDto createJournal(Long userId, JournalRequestDto request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    Journal journal = Journal.builder().user(user).ticker(request.ticker())
        .entryTime(request.entryTime()).exitTime(request.exitTime()).position(request.position())
        .entryPrice(request.entryPrice()).exitPrice(request.exitPrice())
        .leverage(request.leverage()).volume(request.volume()).fee(request.fee())
        .entryReason(request.entryReason()).exitReason(request.exitReason())
        .emotionTag(request.emotionTag()).build();

    Integer hmmScore = calculateAverageHmmScore(request.entryTime(), request.exitTime());
    journal.calculateMetrics(hmmScore);

    // 일지 저장
    journalRepository.save(journal);

    // 자동 리포트 발행
    int unreportedCount = journalRepository.countByUserIdAndIsReportedFalse(userId);
    int batchSize = user.getReportBatchSize();

    if (unreportedCount >= batchSize) {
      // 트랜잭션 꼬임 방지
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          CompletableFuture.runAsync(() -> {
            try {
              reportService.generatePerformanceReport(userId);
            } catch (Exception e) {
              log.error("자동 리포트 발행 중 오류 발생 (UserId: {})", userId, e);
            }
          });
        }
      });
    }

    return new JournalIdResponseDto(journal.getId());
  }

  public Page<JournalListResponseDto> getJournals(Long userId, Pageable pageable) {
    return journalRepository.findAllByUserIdOrderByEntryTimeDesc(userId, pageable)
        .map(JournalListResponseDto::new);
  }

  public JournalDetailResponseDto getJournalDetail(Long userId, Long journalId) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);

    List<HmmHistory> history = hmmHistoryRepository
        .findByTimestampBetweenOrderByTimestampAsc(journal.getEntryTime(), journal.getExitTime());
    List<HmmChartDto> chartData =
        history.stream().map(HmmChartDto::new).collect(Collectors.toList());

    return new JournalDetailResponseDto(journal, chartData);
  }

  @Transactional
  public void updateJournal(Long userId, Long journalId, JournalRequestDto request) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);

    journal.updateJournal(request.ticker(), request.entryTime(), request.exitTime(),
        request.position(), request.entryPrice(), request.exitPrice(), request.leverage(),
        request.volume(), request.fee(), request.entryReason(), request.exitReason(),
        request.emotionTag());

    Integer hmmScore = calculateAverageHmmScore(request.entryTime(), request.exitTime());
    journal.calculateMetrics(hmmScore);
  }

  @Transactional
  public void deleteJournal(Long userId, Long journalId) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);
    journalRepository.delete(journal);
  }

  private Integer calculateAverageHmmScore(LocalDateTime entryTime, LocalDateTime exitTime) {
    List<HmmHistory> history =
        hmmHistoryRepository.findByTimestampBetweenOrderByTimestampAsc(entryTime, exitTime);

    if (history.isEmpty()) {
      return hmmHistoryRepository.findTopByTimestampLessThanEqualOrderByTimestampDesc(entryTime)
          .map(HmmHistory::getTrendScore).orElse(50);
    }

    double avg = history.stream().mapToInt(HmmHistory::getTrendScore).average().orElse(50.0);
    return (int) Math.round(avg);
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
