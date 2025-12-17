package com.mx.att.digital.identity.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void initValidator() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void closeValidator() {
    if (factory != null) factory.close();
  }

  @Test
  @DisplayName("record stores values (accessors)")
  void accessors_return_values() {
    OffsetDateTime ts = OffsetDateTime.now();
    ErrorResponse resp = new ErrorResponse("ERROR", "boom", true, "E123", ts);

    assertThat(resp.status()).isEqualTo("ERROR");
    assertThat(resp.message()).isEqualTo("boom");
    assertThat(resp.retryable()).isTrue();
    assertThat(resp.errorCode()).isEqualTo("E123");
    assertThat(resp.timestamp()).isSameAs(ts);
  }

  @Test
  @DisplayName("validation passes when all fields are present and non-blank")
  void validation_ok() {
    ErrorResponse resp = new ErrorResponse("ERROR", "boom", false, "E123", OffsetDateTime.now());

    Set<ConstraintViolation<ErrorResponse>> violations = validator.validate(resp);

    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("@NotBlank status -> violation when blank")
  void status_blank_is_invalid() {
    ErrorResponse resp = new ErrorResponse("   ", "msg", true, "E1", OffsetDateTime.now());

    Set<ConstraintViolation<ErrorResponse>> violations = validator.validate(resp);

    assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("status");
  }

  @Test
  @DisplayName("@NotBlank message -> violation when null/blank")
  void message_null_or_blank_is_invalid() {
    ErrorResponse nullMsg = new ErrorResponse("ERROR", null, true, "E1", OffsetDateTime.now());
    ErrorResponse blankMsg = new ErrorResponse("ERROR", "", true, "E1", OffsetDateTime.now());

    Set<ConstraintViolation<ErrorResponse>> v1 = validator.validate(nullMsg);
    Set<ConstraintViolation<ErrorResponse>> v2 = validator.validate(blankMsg);

    assertThat(v1).extracting(v -> v.getPropertyPath().toString()).contains("message");
    assertThat(v2).extracting(v -> v.getPropertyPath().toString()).contains("message");
  }

  @Test
  @DisplayName("@NotNull retryable -> violation when null")
  void retryable_null_is_invalid() {
    ErrorResponse resp = new ErrorResponse("ERROR", "msg", null, "E1", OffsetDateTime.now());

    Set<ConstraintViolation<ErrorResponse>> violations = validator.validate(resp);

    assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("retryable");
  }

  @Test
  @DisplayName("@NotBlank errorCode -> violation when null/blank")
  void errorCode_null_or_blank_is_invalid() {
    ErrorResponse nullCode = new ErrorResponse("ERROR", "msg", true, null, OffsetDateTime.now());
    ErrorResponse blankCode = new ErrorResponse("ERROR", "msg", true, "   ", OffsetDateTime.now());

    Set<ConstraintViolation<ErrorResponse>> v1 = validator.validate(nullCode);
    Set<ConstraintViolation<ErrorResponse>> v2 = validator.validate(blankCode);

    assertThat(v1).extracting(v -> v.getPropertyPath().toString()).contains("errorCode");
    assertThat(v2).extracting(v -> v.getPropertyPath().toString()).contains("errorCode");
  }

  @Test
  @DisplayName("@NotNull timestamp -> violation when null")
  void timestamp_null_is_invalid() {
    ErrorResponse resp = new ErrorResponse("ERROR", "msg", true, "E1", null);

    Set<ConstraintViolation<ErrorResponse>> violations = validator.validate(resp);

    assertThat(violations).extracting(v -> v.getPropertyPath().toString()).contains("timestamp");
  }
}
