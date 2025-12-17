package com.mx.att.digital.identity.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.LambdaClientBuilder;

import java.net.URI;

@Configuration
public class AwsLambdaConfig {

  private static final Logger log = LoggerFactory.getLogger(AwsLambdaConfig.class);

  @Bean
  public AwsCredentialsProvider awsCredentialsProvider(
      @Value("${aws.credentials.provider:default}") String provider,
      @Value("${aws.credentials.access-key:}") String accessKey,
      @Value("${aws.credentials.secret-key:}") String secretKey
  ) {
    log.info("[AWS] provider={} accessKey.len={} secretKey.len={}",
        provider,
        accessKey == null ? -1 : accessKey.length(),
        secretKey == null ? -1 : secretKey.length()
    );

    if ("static".equalsIgnoreCase(provider)) {
      return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }
    return DefaultCredentialsProvider.create();
  }

  @Bean
  public LambdaClient lambdaClient(
      AwsCredentialsProvider credentialsProvider,
      @Value("${aws.region}") String region,
      @Value("${aws.lambda.endpoint-override:}") String endpointOverride,
      @Value("${aws.lambda.arn:}") String legacyArn // ✅ opcional para tests
  ) {
    log.info("[AWS] region={}", region);

    String overrideCandidate = (endpointOverride != null && !endpointOverride.isBlank())
        ? endpointOverride
        : legacyArn;

    LambdaClientBuilder builder = LambdaClient.builder()
        .region(Region.of(region))
        .credentialsProvider(credentialsProvider);

    if (overrideCandidate != null && !overrideCandidate.isBlank()) {
      if (overrideCandidate.startsWith("arn:")) {
        log.warn("[AWS] Ignorando endpoint override porque parece ARN: {}", overrideCandidate);
      } else {
        builder = builder.endpointOverride(URI.create(overrideCandidate));
      }
    }

    return builder.build();
  }
}
