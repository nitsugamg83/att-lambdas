package com.mx.att.digital.identity.config;

import org.junit.jupiter.api.Test;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;

import static org.assertj.core.api.Assertions.assertThat;

class AwsLambdaConfigTest {

  private final AwsLambdaConfig config = new AwsLambdaConfig();

  @Test
  void awsCredentialsProvider_static_branch() {
    AwsCredentialsProvider provider = config.awsCredentialsProvider(
        "static",
        "ak",
        "sk"
    );

    assertThat(provider).isInstanceOf(StaticCredentialsProvider.class);
  }

  @Test
  void awsCredentialsProvider_default_branch_when_not_static() {
    AwsCredentialsProvider provider = config.awsCredentialsProvider(
        "default",
        "",
        ""
    );

    assertThat(provider).isInstanceOf(DefaultCredentialsProvider.class);
  }

  @Test
  void lambdaClient_with_endpoint_override_branch() {
    AwsCredentialsProvider provider = config.awsCredentialsProvider("static", "ak", "sk");

    LambdaClient client = config.lambdaClient(provider, "us-east-1", "http://localhost:4566");

    assertThat(client).isNotNull();
    client.close();
  }

  @Test
  void lambdaClient_without_endpoint_override_branch_blank() {
    AwsCredentialsProvider provider = config.awsCredentialsProvider("static", "ak", "sk");

    // endpointOverride vacío => no entra al if (endpointOverride != null && !blank)
    LambdaClient client = config.lambdaClient(provider, "us-east-1", "");

    assertThat(client).isNotNull();
    client.close();
  }

  @Test
  void lambdaClient_without_endpoint_override_branch_null() {
    AwsCredentialsProvider provider = config.awsCredentialsProvider("static", "ak", "sk");

    // null => tampoco entra al if
    LambdaClient client = config.lambdaClient(provider, "us-east-1", null);

    assertThat(client).isNotNull();
    client.close();
  }
}
