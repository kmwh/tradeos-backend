package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.journal.entity.enums.Position;
import io.github.kmwh.tradeos_backend.journal.entity.enums.EmotionTag;

public record JournalRequestDto(String ticker, LocalDateTime entryTime, LocalDateTime exitTime,
    Position position, Double entryPrice, Double exitPrice, Double leverage, Double volume,
    Double fee, String entryReason, String exitReason, EmotionTag emotionTag) {
}
