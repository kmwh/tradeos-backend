package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;

public record JournalListResponseDto(Long journalId, LocalDateTime entryTime, String position,
    Double roi, Boolean isAnalyzed) {
  public JournalListResponseDto(Journal journal) {
    this(journal.getId(), journal.getEntryTime(), journal.getPosition(), journal.getRoi(),
        journal.getIsAnalyzed());
  }
}
