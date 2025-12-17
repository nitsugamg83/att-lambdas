package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record SessionInitRequest(
    @NotBlank String uuid,
    @NotNull OffsetDateTime timestamp,
    String msisdn
) {}
