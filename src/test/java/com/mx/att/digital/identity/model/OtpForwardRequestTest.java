package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OtpForwardRequestTest {

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
    OtpForwardRequest req = new OtpForwardRequest("uuid-1", ts, "1234567890");

    assertThat(req.uuid()).isEqualTo("uuid-1");
    assertThat(req.timestamp()).isSameAs(ts);
    assertThat(req.msisdn()).isEqualTo("1234567890");
  }

  @Test
  void validation_ok() {
    OtpForwardRequest req = new OtpForwardRequest("uuid-1", OffsetDateTime.now(), "1234567890");
    assertThat(validator.validate(req)).isEmpty();
  }

  @Test
  void uuid_blank_invalid() {
    OtpForwardRequest req = new OtpForwardRequest(" ", OffsetDateTime.now(), "1234567890");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("uuid");
  }

  @Test
  void timestamp_null_invalid() {
    OtpForwardRequest req = new OtpForwardRequest("uuid-1", null, "1234567890");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("timestamp");
  }

  @Test
  void msisdn_invalid_pattern() {
    OtpForwardRequest req = new OtpForwardRequest("uuid-1", OffsetDateTime.now(), "123");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("msisdn");
  }

  @Test
  void msisdn_null_allowed_by_pattern() {
    OtpForwardRequest req = new OtpForwardRequest("uuid-1", OffsetDateTime.now(), null);
    assertThat(validator.validate(req)).isEmpty();
  }
}
