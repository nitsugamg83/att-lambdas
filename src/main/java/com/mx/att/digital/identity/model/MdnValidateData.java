package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MdnValidateData(
    @NotBlank String uuid,
    @NotBlank String resultCode,
    @NotBlank String resultDesc,
    @NotBlank String msisdn,
    @NotBlank String customerType,
    @NotBlank String platform,
    @NotNull Boolean isRegistered,
    @NotBlank String status
) {}
