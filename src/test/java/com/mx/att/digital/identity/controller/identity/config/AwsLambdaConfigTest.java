package com.mx.att.digital.identity.controller.identity.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mx.att.digital.identity.config.AwsLambdaConfig;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AwsLambdaConfig.class)
@TestPropertySource(properties = {
        "aws.region=us-east-1",
        "aws.credentials.provider=static",
        "aws.credentials.access-key=test",
        "aws.credentials.secret-key=test",
        "aws.lambda.endpoint-override=http://localhost:4566"
})
class AwsLambdaConfigTest {

    @Autowired
    AwsCredentialsProvider credentialsProvider;

    @Autowired
    LambdaClient lambdaClient;

    @Test
    void static_credentials_provider_is_used() {
        assertThat(credentialsProvider)
                .isInstanceOf(StaticCredentialsProvider.class);
    }

    @Test
    void lambda_client_is_created() {
        assertThat(lambdaClient).isNotNull();
    }
}

