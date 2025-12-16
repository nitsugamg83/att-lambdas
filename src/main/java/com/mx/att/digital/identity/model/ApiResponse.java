package com.mx.att.digital.identity.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.function.BooleanSupplier;

public record ApiResponse<T>(
    @NotBlank String status,
    @NotBlank String message,
    @NotNull T data,
    @NotNull OffsetDateTime timestamp
) {

    public BooleanSupplier isSuccess() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isSuccess'");
    }

    public void setSuccess(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSuccess'");
    }

    public void setData(SessionInitData data2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }

    public static Object builder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'builder'");
    }

    public void setError(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setError'");
    }}
