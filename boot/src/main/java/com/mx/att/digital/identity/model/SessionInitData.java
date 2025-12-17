package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;

public record SessionInitData(
    @NotBlank String uuid,
    @NotBlank String resultCode,
    @NotBlank String resultDesc
) {}
