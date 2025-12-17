package com.mx.att.digital.identity.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.att.digital.identity.model.ApiResponse;
import com.mx.att.digital.identity.model.MdnValidateData;
import com.mx.att.digital.identity.model.MdnValidateRequest;
import com.mx.att.digital.identity.model.OtpRequest;
import com.mx.att.digital.identity.model.OtpRequestData;
import com.mx.att.digital.identity.model.OtpValidateRequest;
import org.junit.jupiter.api.BeforeEach;
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
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
  // Flujo exitoso (sube coverage)
  // =========================
  @Test
  void mdnValidate_success_returns_parsed_response() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    ApiResponse<MdnValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    // OJO: no uses eq("{...}") porque el body puede variar (espacios/orden/campos)
    when(objectMapper.readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
    )).thenReturn(expected);

    MdnValidateRequest req = new MdnValidateRequest(null, null, null, null, null);

    ApiResponse<MdnValidateData> out = client.mdnValidate(req);

    assertThat(out).isSameAs(expected);

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verify(objectMapper).readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
    );
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  // =========================
  // LambdaException se propaga
  // =========================
  @Test
  void invoke_lambdaException_propagated() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    when(lambdaClient.invoke(any(InvokeRequest.class)))
        .thenThrow(LambdaException.builder().message("AWS error").build());

    MdnValidateRequest req = new MdnValidateRequest(null, null, null, null, null);

    assertThrows(LambdaException.class, () -> client.mdnValidate(req));

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  // =========================
  // Lambda functionError -> RuntimeException
  // =========================
  @Test
  void invoke_functionError_throwsRuntimeException() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .functionError("Unhandled")
        .payload(SdkBytes.fromString("{}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    OtpRequest req = new OtpRequest(null, null, null);

    assertThrows(RuntimeException.class, () -> client.otpRequest(req));

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  // =========================
  // Payload vacío -> RuntimeException
  // =========================
  @Test
  void invoke_emptyPayload_throwsRuntimeException() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    OtpValidateRequest req = new OtpValidateRequest(null, null, null, null);

    assertThrows(RuntimeException.class, () -> client.otpValidate(req));

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  // =========================
  // Error de parseo -> RuntimeException
  // =========================
  @Test
  void invoke_parseError_throwsRuntimeException() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("{\"bad\":true}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    when(objectMapper.readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<OtpRequestData>>>any()
    )).thenThrow(new IllegalArgumentException("boom"));

    OtpRequest req = new OtpRequest(null, null, null);

    assertThrows(RuntimeException.class, () -> client.otpRequest(req));

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verify(objectMapper).readValue(
        anyString(),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<OtpRequestData>>>any()
    );
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }
}
