package io.github.kmwh.tradeos_backend.quant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record HmmPredictResponseDto(LocalDateTime timestamp, String symbol,
    @JsonProperty("trend_score") Integer trendScore) {
}
