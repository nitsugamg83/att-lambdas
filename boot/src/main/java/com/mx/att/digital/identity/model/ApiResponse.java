package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ApiResponse<T>(
    @NotBlank String status,
    @NotBlank String message,
    @NotNull T data,
    @NotNull OffsetDateTime timestamp
) {}