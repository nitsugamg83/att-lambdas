package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class OtpValidateDataTest {

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
    OtpValidateData data = new OtpValidateData("uuid-1", "0", "OK", "https://x");
    assertThat(data.uuid()).isEqualTo("uuid-1");
    assertThat(data.resultCode()).isEqualTo("0");
    assertThat(data.resultDesc()).isEqualTo("OK");
    assertThat(data.onboardingUrl()).isEqualTo("https://x");
  }

  @Test
  void validation_ok() {
    assertThat(validator.validate(new OtpValidateData("uuid-1", "0", "OK", "https://x"))).isEmpty();
  }

  @Test
  void onboardingUrl_blank_invalid() {
    assertThat(validator.validate(new OtpValidateData("uuid-1", "0", "OK", " ")))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("onboardingUrl");
  }
}
