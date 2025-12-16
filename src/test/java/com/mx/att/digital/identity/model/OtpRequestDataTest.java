package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class OtpRequestDataTest {

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
    OtpRequestData data = new OtpRequestData("uuid-1", "0", "OK");
    assertThat(data.uuid()).isEqualTo("uuid-1");
    assertThat(data.resultCode()).isEqualTo("0");
    assertThat(data.resultDesc()).isEqualTo("OK");
  }

  @Test
  void validation_ok() {
    assertThat(validator.validate(new OtpRequestData("uuid-1", "0", "OK"))).isEmpty();
  }

  @Test
  void uuid_blank_invalid() {
    assertThat(validator.validate(new OtpRequestData(" ", "0", "OK")))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("uuid");
  }

  @Test
  void resultCode_blank_invalid() {
    assertThat(validator.validate(new OtpRequestData("uuid-1", "", "OK")))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("resultCode");
  }

  @Test
  void resultDesc_blank_invalid() {
    assertThat(validator.validate(new OtpRequestData("uuid-1", "0", "   ")))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("resultDesc");
  }
}
