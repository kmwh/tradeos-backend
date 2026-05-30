package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;

public record JournalRequestDto(String ticker, LocalDateTime entryTime, LocalDateTime exitTime,
    String position, Double entryPrice, Double exitPrice, Double leverage, Double realizedPnl,
    Double roi, String entryReason, String exitReason, String emotionTag) {
}
