package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;

public record MdnValidateRequest(
    @NotBlank String uuid,
    @NotNull OffsetDateTime timestamp,
    @NotBlank String source,
    @Pattern(regexp = "\\d{10}") String msisdn
) {}
