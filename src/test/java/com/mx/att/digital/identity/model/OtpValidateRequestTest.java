package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OtpValidateRequestTest {

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
    OtpValidateRequest req = new OtpValidateRequest("uuid-1", ts, "1234567890", "123456");

    assertThat(req.uuid()).isEqualTo("uuid-1");
    assertThat(req.timestamp()).isSameAs(ts);
    assertThat(req.msisdn()).isEqualTo("1234567890");
    assertThat(req.otp()).isEqualTo("123456");
  }

  @Test
  void validation_ok() {
    OtpValidateRequest req = new OtpValidateRequest("uuid-1", OffsetDateTime.now(), "1234567890", "123456");
    assertThat(validator.validate(req)).isEmpty();
  }

  @Test
  void uuid_blank_invalid() {
    OtpValidateRequest req = new OtpValidateRequest(" ", OffsetDateTime.now(), "1234567890", "123456");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("uuid");
  }

  @Test
  void timestamp_null_invalid() {
    OtpValidateRequest req = new OtpValidateRequest("uuid-1", null, "1234567890", "123456");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("timestamp");
  }

  @Test
  void msisdn_invalid_pattern() {
    OtpValidateRequest req = new OtpValidateRequest("uuid-1", OffsetDateTime.now(), "abc", "123456");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("msisdn");
  }

  @Test
  void otp_invalid_pattern() {
    OtpValidateRequest req = new OtpValidateRequest("uuid-1", OffsetDateTime.now(), "1234567890", "12");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("otp");
  }

  @Test
  void msisdn_and_otp_null_allowed_by_pattern() {
    OtpValidateRequest req = new OtpValidateRequest("uuid-1", OffsetDateTime.now(), null, null);
    assertThat(validator.validate(req)).isEmpty();
  }
}
