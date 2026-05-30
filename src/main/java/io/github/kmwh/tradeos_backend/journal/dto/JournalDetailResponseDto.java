package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;
import java.util.List;
import io.github.kmwh.tradeos_backend.journal.entity.Journal;
import io.github.kmwh.tradeos_backend.journal.entity.enums.Position;
import io.github.kmwh.tradeos_backend.journal.entity.enums.EmotionTag;
import io.github.kmwh.tradeos_backend.quant.dto.HmmChartDto;

public record JournalDetailResponseDto(Long journalId, String ticker, LocalDateTime entryTime,
    LocalDateTime exitTime, Position position, Double entryPrice, Double exitPrice, Double leverage,
    Double volume, Double fee, Double realizedPnl, Double roi, String entryReason,
    String exitReason, EmotionTag emotionTag, List<HmmChartDto> hmmChartData) {
  public JournalDetailResponseDto(Journal journal, List<HmmChartDto> hmmChartData) {
    this(journal.getId(), journal.getTicker(), journal.getEntryTime(), journal.getExitTime(),
        journal.getPosition(), journal.getEntryPrice(), journal.getExitPrice(),
        journal.getLeverage(), journal.getVolume(), journal.getFee(), journal.getRealizedPnl(),
        journal.getRoi(), journal.getEntryReason(), journal.getExitReason(),
        journal.getEmotionTag(), hmmChartData);
  }
}
