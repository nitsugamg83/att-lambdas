package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;

public record OtpRequest(
    @NotBlank String uuid,
    @NotBlank String sourceSystem,
    @Pattern(regexp = "\\d{10}") String msisdn,
    @NotNull OffsetDateTime timestamp
) {}
