package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MdnValidateRequestTest {

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
    MdnValidateRequest req = new MdnValidateRequest("uuid-1", ts, "src", "flow", "1234567890");

    assertThat(req.uuid()).isEqualTo("uuid-1");
    assertThat(req.timestamp()).isSameAs(ts);
    assertThat(req.source()).isEqualTo("src");
    assertThat(req.initFlowType()).isEqualTo("flow");
    assertThat(req.msisdn()).isEqualTo("1234567890");
  }

  @Test
  void validation_ok() {
    MdnValidateRequest req = new MdnValidateRequest("uuid-1", OffsetDateTime.now(), "src", "flow", "1234567890");
    assertThat(validator.validate(req)).isEmpty();
  }

  @Test
  void notBlank_fields_invalid_when_blank() {
    MdnValidateRequest req = new MdnValidateRequest(" ", OffsetDateTime.now(), " ", " ", "1234567890");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("uuid", "source", "initFlowType");
  }

  @Test
  void timestamp_null_invalid() {
    MdnValidateRequest req = new MdnValidateRequest("uuid-1", null, "src", "flow", "1234567890");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("timestamp");
  }

  @Test
  void msisdn_invalid_pattern() {
    MdnValidateRequest req = new MdnValidateRequest("uuid-1", OffsetDateTime.now(), "src", "flow", "abc");
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("msisdn");
  }

  @Test
  void msisdn_null_allowed_by_pattern() {
    MdnValidateRequest req = new MdnValidateRequest("uuid-1", OffsetDateTime.now(), "src", "flow", null);
    assertThat(validator.validate(req)).isEmpty();
  }
}
