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

  private static final OffsetDateTime TS = OffsetDateTime.parse("2025-01-01T10:00:00Z");

  @BeforeAll
  static void initValidator() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void closeValidator() {
    if (factory != null) factory.close();
  }

  private static <T> Set<ConstraintViolation<ApiResponse<T>>> validate(ApiResponse<T> resp) {
    return validator.validate(resp);
  }

  @Test
  @DisplayName("record stores values (accessors)")
  void accessors_return_values() {
    ApiResponse<String> resp = new ApiResponse<>("OK", "msg", "data", TS);

    assertThat(resp.status()).isEqualTo("OK");
    assertThat(resp.message()).isEqualTo("msg");
    assertThat(resp.data()).isEqualTo("data");
    assertThat(resp.timestamp()).isSameAs(TS);
  }

  @Test
  @DisplayName("validation passes when all fields are present and non-blank")
  void validation_ok() {
    ApiResponse<String> resp = new ApiResponse<>("OK", "All good", "data", TS);

    assertThat(validate(resp)).isEmpty();
  }

  @Test
  @DisplayName("@NotBlank status -> violation when blank")
  void status_blank_is_invalid() {
    ApiResponse<String> resp = new ApiResponse<>("   ", "msg", "data", TS);

    assertThat(validate(resp))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("status");
  }

  @Test
  @DisplayName("@NotBlank message -> violation when null or blank")
  void message_null_or_blank_is_invalid() {
    ApiResponse<String> nullMsg = new ApiResponse<>("OK", null, "data", TS);
    ApiResponse<String> blankMsg = new ApiResponse<>("OK", "", "data", TS);

    assertThat(validate(nullMsg))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("message");

    assertThat(validate(blankMsg))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("message");
  }

  @Test
  @DisplayName("@NotNull data -> violation when null")
  void data_null_is_invalid() {
    ApiResponse<String> resp = new ApiResponse<>("OK", "msg", null, TS);

    assertThat(validate(resp))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("data");
  }

  @Test
  @DisplayName("@NotNull timestamp -> violation when null")
  void timestamp_null_is_invalid() {
    ApiResponse<String> resp = new ApiResponse<>("OK", "msg", "data", null);

    assertThat(validate(resp))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("timestamp");
  }
}
