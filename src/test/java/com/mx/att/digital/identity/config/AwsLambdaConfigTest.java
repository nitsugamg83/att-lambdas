package com.mx.att.digital.identity.config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AwsLambdaConfigTest {

  private final AwsLambdaConfig config = new AwsLambdaConfig();

  // === awsCredentialsProvider parametrizado (reemplaza 2 tests repetidos) ===
  static Stream<CredentialsCase> credentialsProviderCases() {
    return Stream.of(
        new CredentialsCase("static",  "ak", "sk", StaticCredentialsProvider.class),
        new CredentialsCase("default", "",   "",   DefaultCredentialsProvider.class)
    );
  }

  @ParameterizedTest
  @MethodSource("credentialsProviderCases")
  void awsCredentialsProvider_branches(CredentialsCase c) {
    AwsCredentialsProvider provider = config.awsCredentialsProvider(
        c.mode(),
        c.accessKey(),
        c.secretKey()
    );

    assertThat(provider).isInstanceOf(c.expectedType());
  }

  // === lambdaClient parametrizado (reemplaza los 3 tests repetidos) ===
  static Stream<String> endpointOverrides() {
    return Stream.of(
        "http://localhost:4566", // entra al if
        "",                      // no entra al if
        null                     // no entra al if
    );
  }

  @ParameterizedTest
  @MethodSource("endpointOverrides")
  void lambdaClient_endpoint_override_variants(String endpointOverride) {
    AwsCredentialsProvider provider = config.awsCredentialsProvider("static", "ak", "sk");

    try (LambdaClient client = config.lambdaClient(provider, "us-east-1", endpointOverride)) {
      assertThat(client).isNotNull();
    }
  }

  // record para mantener los parámetros del test claros y tipados
  record CredentialsCase(
      String mode,
      String accessKey,
      String secretKey,
      Class<?> expectedType
  ) {}
}
