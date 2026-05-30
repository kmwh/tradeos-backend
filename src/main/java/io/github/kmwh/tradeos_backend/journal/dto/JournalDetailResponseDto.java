package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;

public record JournalDetailResponseDto(Long journalId, String ticker, LocalDateTime entryTime,
    LocalDateTime exitTime, String position, Double entryPrice, Double exitPrice, Double leverage,
    Double realizedPnl, Double roi, String entryReason, String exitReason, String emotionTag) {

  public JournalDetailResponseDto(Journal journal) {
    this(journal.getId(), journal.getTicker(), journal.getEntryTime(), journal.getExitTime(),
        journal.getPosition(), journal.getEntryPrice(), journal.getExitPrice(),
        journal.getLeverage(), journal.getRealizedPnl(), journal.getRoi(), journal.getEntryReason(),
        journal.getExitReason(), journal.getEmotionTag());
  }
}
