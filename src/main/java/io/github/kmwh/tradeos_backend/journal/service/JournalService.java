package io.github.kmwh.tradeos_backend.journal.service;

import io.github.kmwh.tradeos_backend.journal.dto.*;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.repository.JournalRepository;
import io.github.kmwh.tradeos_backend.quant.entity.HmmHistory;
import io.github.kmwh.tradeos_backend.quant.repository.HmmHistoryRepository;
import io.github.kmwh.tradeos_backend.user.entity.User;
import io.github.kmwh.tradeos_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JournalService {

  private final JournalRepository journalRepository;
  private final UserRepository userRepository;
  private final HmmHistoryRepository hmmHistoryRepository;

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

    Integer hmmScore = hmmHistoryRepository
        .findTopByTimestampLessThanEqualOrderByTimestampDesc(request.entryTime())
        .map(HmmHistory::getTrendScore).orElse(50);

    journal.calculateMetrics(hmmScore);

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

    journal.updateJournal(request.ticker(), request.entryTime(), request.exitTime(),
        request.position(), request.entryPrice(), request.exitPrice(), request.leverage(),
        request.volume(), request.fee(), request.entryReason(), request.exitReason(),
        request.emotionTag());

    Integer hmmScore = hmmHistoryRepository
        .findTopByTimestampLessThanEqualOrderByTimestampDesc(request.entryTime())
        .map(HmmHistory::getTrendScore).orElse(50);

    journal.calculateMetrics(hmmScore);
  }

  @Transactional
  public void deleteJournal(Long userId, Long journalId) {
    Journal journal = getJournalAndCheckOwnership(userId, journalId);
    journalRepository.delete(journal);
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
