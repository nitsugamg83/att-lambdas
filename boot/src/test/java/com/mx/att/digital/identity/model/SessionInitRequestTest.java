package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SessionInitRequestTest {

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
    SessionInitRequest req = new SessionInitRequest("uuid-1", ts, "1234567890");

    assertThat(req.uuid()).isEqualTo("uuid-1");
    assertThat(req.timestamp()).isSameAs(ts);
    assertThat(req.msisdn()).isEqualTo("1234567890");
  }

  @Test
  void validation_ok() {
    SessionInitRequest req = new SessionInitRequest("uuid-1", OffsetDateTime.now(), null);
    assertThat(validator.validate(req)).isEmpty();
  }

  @Test
  void uuid_blank_invalid() {
    SessionInitRequest req = new SessionInitRequest(" ", OffsetDateTime.now(), null);
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("uuid");
  }

  @Test
  void timestamp_null_invalid() {
    SessionInitRequest req = new SessionInitRequest("uuid-1", null, null);
    assertThat(validator.validate(req))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("timestamp");
  }
}
