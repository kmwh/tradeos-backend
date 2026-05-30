package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.entity.enums.Position;

public record JournalListResponseDto(Long journalId, String ticker, LocalDateTime entryTime,
    LocalDateTime exitTime, Position position, Double leverage, Double volume, Double realizedPnl,
    Double roi) {

  public JournalListResponseDto(Journal journal) {
    this(journal.getId(), journal.getTicker(), journal.getEntryTime(), journal.getExitTime(),
        journal.getPosition(), journal.getLeverage(), journal.getVolume(), journal.getRealizedPnl(),
        journal.getRoi());
  }
}
