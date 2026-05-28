package io.github.kmwh.tradeos_backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class HmmPredictResponseDto {
    private LocalDateTime timestamp;
    private String symbol;
    private Integer trend_score;
}