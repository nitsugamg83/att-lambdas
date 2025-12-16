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

class ApiResponseTest {

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
    ApiResponse<String> resp = new ApiResponse<>("OK", "msg", "data", ts);

    assertThat(resp.status()).isEqualTo("OK");
    assertThat(resp.message()).isEqualTo("msg");
    assertThat(resp.data()).isEqualTo("data");
    assertThat(resp.timestamp()).isSameAs(ts);
  }

  @Test
  @DisplayName("validation passes when all fields are present and non-blank")
  void validation_ok() {
    ApiResponse<String> resp = new ApiResponse<>("OK", "All good", "data", OffsetDateTime.now());

    Set<ConstraintViolation<ApiResponse<String>>> violations = validator.validate(resp);

    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("@NotBlank status -> violation when blank")
  void status_blank_is_invalid() {
    ApiResponse<String> resp = new ApiResponse<>("   ", "msg", "data", OffsetDateTime.now());

    Set<ConstraintViolation<ApiResponse<String>>> violations = validator.validate(resp);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .contains("status");
  }

  @Test
  @DisplayName("@NotBlank message -> violation when null or blank")
  void message_null_or_blank_is_invalid() {
    ApiResponse<String> nullMsg = new ApiResponse<>("OK", null, "data", OffsetDateTime.now());
    ApiResponse<String> blankMsg = new ApiResponse<>("OK", "", "data", OffsetDateTime.now());

    Set<ConstraintViolation<ApiResponse<String>>> v1 = validator.validate(nullMsg);
    Set<ConstraintViolation<ApiResponse<String>>> v2 = validator.validate(blankMsg);

    assertThat(v1).extracting(v -> v.getPropertyPath().toString()).contains("message");
    assertThat(v2).extracting(v -> v.getPropertyPath().toString()).contains("message");
  }

  @Test
  @DisplayName("@NotNull data -> violation when null")
  void data_null_is_invalid() {
    ApiResponse<String> resp = new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    Set<ConstraintViolation<ApiResponse<String>>> violations = validator.validate(resp);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .contains("data");
  }

  @Test
  @DisplayName("@NotNull timestamp -> violation when null")
  void timestamp_null_is_invalid() {
    ApiResponse<String> resp = new ApiResponse<>("OK", "msg", "data", null);

    Set<ConstraintViolation<ApiResponse<String>>> violations = validator.validate(resp);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .contains("timestamp");
  }
}
