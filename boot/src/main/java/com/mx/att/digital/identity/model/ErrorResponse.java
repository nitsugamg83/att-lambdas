package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ErrorResponse(
    @NotBlank String status,
    @NotBlank String message,
    @NotNull Boolean retryable,
    @NotBlank String errorCode,
    @NotNull OffsetDateTime timestamp
) {}
