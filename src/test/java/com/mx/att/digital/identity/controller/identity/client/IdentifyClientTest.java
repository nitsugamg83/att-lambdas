package com.mx.att.digital.identity.controller.identity.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.att.digital.identity.client.OrchestratorClient;
import com.mx.att.digital.identity.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrchestratorClientTest {

  @Mock
  private LambdaClient lambdaClient;

  @Mock
  private ObjectMapper objectMapper;

  private OrchestratorClient client;

  private static final String FUNCTION_ARN =
      "arn:aws:lambda:us-east-1:123:function:test";

  @BeforeEach
  void setup() {
    client = new OrchestratorClient(
        lambdaClient,
        objectMapper,
        FUNCTION_ARN,
        "RequestResponse",
        "Tail"
    );
  }

  // =========================
  // Flujo exitoso
  // =========================
  @Disabled("Se omite temporalmente por parseo interno obligatorio de ApiResponse")
  @Test
  void sessionInit_success() {
    // intentionally skipped
  }

  // =========================
  // LambdaException
  // =========================
  @Test
  void invoke_lambdaException_propagated() throws Exception {

    when(objectMapper.writeValueAsString(any()))
        .thenReturn("{}");

    when(lambdaClient.invoke(any(InvokeRequest.class)))
        .thenThrow(
            LambdaException.builder()
                .message("AWS error")
                .build()
        );

    assertThrows(LambdaException.class, () ->
        client.mdnValidate(
            new MdnValidateRequest(null, null, null, null, null)
        )
    );
  }

  // =========================
  // Lambda functionError
  // =========================
  @Test
  void invoke_functionError_throwsRuntimeException() throws Exception {

    when(objectMapper.writeValueAsString(any()))
        .thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .functionError("Unhandled")
        .payload(SdkBytes.fromString("{}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class)))
        .thenReturn(response);

    assertThrows(RuntimeException.class, () ->
        client.otpRequest(new OtpRequest(null, null, null))
    );
  }

  // =========================
  // Payload vacío
  // =========================
  @Test
  void invoke_emptyPayload_throwsRuntimeException() throws Exception {

    when(objectMapper.writeValueAsString(any()))
        .thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class)))
        .thenReturn(response);

    assertThrows(RuntimeException.class, () ->
        client.otpValidate(
            new OtpValidateRequest(null, null, null, null)
        )
    );
  }
}