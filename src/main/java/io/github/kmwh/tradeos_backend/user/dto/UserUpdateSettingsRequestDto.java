package io.github.kmwh.tradeos_backend.user.dto;

import jakarta.validation.constraints.Min;

public record UserUpdateSettingsRequestDto(@Min(value = 2) Integer reportBatchSize) {
}
