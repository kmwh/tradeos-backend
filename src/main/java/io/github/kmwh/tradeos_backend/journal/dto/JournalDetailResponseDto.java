package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.entity.enums.Position;
import io.github.kmwh.tradeos_backend.journal.entity.enums.EmotionTag;

public record JournalDetailResponseDto(Long journalId, String ticker, LocalDateTime entryTime,
    LocalDateTime exitTime, Position position, Double entryPrice, Double exitPrice, Double leverage,
    Double volume, Double fee, Double realizedPnl, Double roi, String entryReason,
    String exitReason, EmotionTag emotionTag) {

  public JournalDetailResponseDto(Journal journal) {
    this(journal.getId(), journal.getTicker(), journal.getEntryTime(), journal.getExitTime(),
        journal.getPosition(), journal.getEntryPrice(), journal.getExitPrice(),
        journal.getLeverage(), journal.getVolume(), journal.getFee(), journal.getRealizedPnl(),
        journal.getRoi(), journal.getEntryReason(), journal.getExitReason(),
        journal.getEmotionTag());
  }
}
