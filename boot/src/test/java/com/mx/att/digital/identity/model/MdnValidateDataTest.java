package com.mx.att.digital.identity.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class MdnValidateDataTest {

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
    MdnValidateData data = new MdnValidateData(
        "uuid-1", "0", "OK", "1234567890", "PREPAID", "matrixx", true, "ACTIVE"
    );

    assertThat(data.uuid()).isEqualTo("uuid-1");
    assertThat(data.resultCode()).isEqualTo("0");
    assertThat(data.resultDesc()).isEqualTo("OK");
    assertThat(data.msisdn()).isEqualTo("1234567890");
    assertThat(data.customerType()).isEqualTo("PREPAID");
    assertThat(data.platform()).isEqualTo("matrixx");
    assertThat(data.isRegistered()).isTrue();
    assertThat(data.status()).isEqualTo("ACTIVE");
  }

  @Test
  void validation_ok() {
    MdnValidateData data = new MdnValidateData(
        "uuid-1", "0", "OK", "1234567890", "PREPAID", "matrixx", false, "ACTIVE"
    );
    assertThat(validator.validate(data)).isEmpty();
  }

  @Test
  void isRegistered_null_invalid() {
    MdnValidateData data = new MdnValidateData(
        "uuid-1", "0", "OK", "1234567890", "PREPAID", "matrixx", null, "ACTIVE"
    );
    assertThat(validator.validate(data))
        .extracting(v -> v.getPropertyPath().toString())
        .contains("isRegistered");
  }
}
