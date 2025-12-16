package com.mx.att.digital.identity.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OtpRequestTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  static void init() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterAll
  static void close() {
    if (factory != null) factory.close();
  }

  @Test
  void accessors_ok() {
    OffsetDateTime ts = OffsetDateTime.now();
    OtpRequest req = new OtpRequest("uuid-1", "1234567890", ts);

    assertThat(req.uuid()).isEqualTo("uuid-1");
    assertThat(req.msisdn()).isEqualTo("1234567890");
    assertThat(req.timestamp()).isSameAs(ts);
  }

  @Test
  void validation_ok() {
    OtpRequest req = new OtpRequest("uuid-1", "1234567890", OffsetDateTime.now());
    assertThat(validator.validate(req)).isEmpty();
  }

  @Test
  void uuid_blank_invalid() {
    OtpRequest req = new OtpRequest("   ", "1234567890", OffsetDateTime.now());
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("uuid");
  }

  @Test
  void timestamp_null_invalid() {
    OtpRequest req = new OtpRequest("uuid-1", "1234567890", null);
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("timestamp");
  }

  @Test
  void msisdn_pattern_invalid_when_not_10_digits() {
    OtpRequest req = new OtpRequest("uuid-1", "123", OffsetDateTime.now());
    Set<ConstraintViolation<OtpRequest>> violations = validator.validate(req);

    assertThat(violations)
        .extracting(v -> v.getPropertyPath().toString())
        .contains("msisdn");
  }

  @Test
  void msisdn_null_is_allowed_by_pattern() {
    // @Pattern permite null por defecto
    OtpRequest req = new OtpRequest("uuid-1", null, OffsetDateTime.now());
    assertThat(validator.validate(req)).isEmpty();
  }
}
