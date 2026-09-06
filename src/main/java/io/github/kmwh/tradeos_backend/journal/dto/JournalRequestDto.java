package io.github.kmwh.tradeos_backend.journal.dto;

import java.time.LocalDateTime;
import io.github.kmwh.tradeos_backend.journal.entity.enums.Position;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import io.github.kmwh.tradeos_backend.journal.entity.enums.EmotionTag;

public record JournalRequestDto(
    @NotBlank(message = "티커는 필수입니다.") String ticker,
    @NotNull(message = "진입 시간은 필수입니다.") LocalDateTime entryTime,
    @NotNull(message = "청산 시간은 필수입니다.") LocalDateTime exitTime,
    @NotNull(message = "포지션은 필수입니다.") Position position,
    @NotNull @Positive(message = "진입 가격은 양수여야 합니다.") Double entryPrice,
    @NotNull @Positive(message = "청산 가격은 양수여야 합니다.") Double exitPrice,
    @NotNull @Min(value = 1, message = "레버리지는 최소 1 이상이어야 합니다.") Double leverage,
    @NotNull @Positive(message = "거래 수량은 양수여야 합니다.") Double volume,
    @PositiveOrZero(message = "수수료는 0 이상이어야 합니다.") Double fee,
    String entryReason,
    String exitReason,
    @NotNull(message = "감정 태그는 필수입니다.") EmotionTag emotionTag
) {
    public JournalRequestDto {
        if (entryTime != null && exitTime != null && exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("청산 시간은 진입 시간보다 이후여야 합니다.");
        }
    }
}