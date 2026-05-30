package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;

public record JournalListResponseDto(Long journalId, String ticker, LocalDateTime entryTime,
    LocalDateTime exitTime, String position, Double leverage, Double roi) {

  public JournalListResponseDto(Journal journal) {
    this(journal.getId(), journal.getTicker(), journal.getEntryTime(), journal.getExitTime(),
        journal.getPosition(), journal.getLeverage(), journal.getRoi());
  }
}
