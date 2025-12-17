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

  // === awsCredentialsProvider parametrizado ===
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

  // === lambdaClient parametrizado para firma de 4 params ===
  static Stream<LambdaCase> lambdaClientCases() {
    return Stream.of(
        // 1) endpointOverride URL -> aplica override
        new LambdaCase("http://localhost:4566", ""),
        // 2) endpointOverride vacío, legacy vacío -> no override
        new LambdaCase("", ""),
        // 3) endpointOverride null, legacy null -> no override
        new LambdaCase(null, null),
        // 4) endpointOverride vacío, legacy es ARN -> debe ignorarse (no debe tronar)
        new LambdaCase("", "arn:aws:lambda:mx-central-1:123456789012:function:test"),
        // 5) endpointOverride es ARN (mal configurado) -> debe ignorarse (no debe tronar)
        new LambdaCase("arn:aws:lambda:mx-central-1:123456789012:function:test", "")
    );
  }

  @ParameterizedTest
  @MethodSource("lambdaClientCases")
  void lambdaClient_endpoint_override_variants(LambdaCase c) {
    AwsCredentialsProvider provider = config.awsCredentialsProvider("static", "ak", "sk");

    try (LambdaClient client = config.lambdaClient(provider, "us-east-1", c.endpointOverride(), c.legacyArn())) {
      assertThat(client).isNotNull();
    }
  }

  // records para mantener parámetros claros
  record CredentialsCase(
      String mode,
      String accessKey,
      String secretKey,
      Class<?> expectedType
  ) {}

  record LambdaCase(
      String endpointOverride,
      String legacyArn
  ) {}
}
