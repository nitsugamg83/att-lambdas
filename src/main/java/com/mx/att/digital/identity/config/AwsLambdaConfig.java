package com.mx.att.digital.identity.config;

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

 
  @Bean
  public AwsCredentialsProvider awsCredentialsProvider(
      @Value("${aws.credentials.provider:default}") String provider,
      @Value("${aws.credentials.access-key:}") String accessKey,
      @Value("${aws.credentials.secret-key:}") String secretKey
  ) {
    if ("static".equalsIgnoreCase(provider)) {
      return StaticCredentialsProvider.create(
          AwsBasicCredentials.create(accessKey, secretKey)
      );
    }
    return DefaultCredentialsProvider.create();
  }

  @Bean
  public LambdaClient lambdaClient(
      AwsCredentialsProvider credentialsProvider,
      @Value("${aws.region}") String region,
      @Value("${aws.lambda.endpoint-override:}") String endpointOverride
  ) {
    LambdaClientBuilder builder = LambdaClient.builder()
        .region(Region.of(region))
        .credentialsProvider(credentialsProvider);

    if (endpointOverride != null && !endpointOverride.isBlank()) {
      builder = builder.endpointOverride(URI.create(endpointOverride));
    }

    return builder.build();
  }
}
