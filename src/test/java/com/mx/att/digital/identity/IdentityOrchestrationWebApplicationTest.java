package com.mx.att.digital.identity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.assertj.core.api.Assertions.assertThatNoException;

class IdentityOrchestrationWebApplicationTest {

  @Test
  void main_runs_without_throwing() {
    assertThatNoException().isThrownBy(() -> {
      try (var ctx = new SpringApplicationBuilder(IdentityOrchestrationWebApplication.class)
          .web(WebApplicationType.NONE)
          // Propiedades mínimas para que no fallen placeholders en beans
          .properties(
              "aws.lambda.function-arn=arn:aws:lambda:us-east-1:000000000000:function:dummy",
              "aws.region=us-east-1"
          )
          .run()) {
        // ok
      }
    });
  }
}
