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
import org.mockito.ArgumentCaptor;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrchestratorClientExceptionTest {

  @Mock private LambdaClient lambdaClient;
  @Mock private ObjectMapper objectMapper;

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

  @Test
  void mdnValidate_success_returns_parsed_response() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
        .logResult("dGVzdA==") // "test" base64
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    ApiResponse<MdnValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(objectMapper.readValue(
        eq("{\"status\":\"OK\"}"),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
    )).thenReturn(expected);

    ApiResponse<MdnValidateData> out =
        client.mdnValidate(new MdnValidateRequest(null, null, null, null, null));

    assertThat(out).isSameAs(expected);

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verify(objectMapper).readValue(
        eq("{\"status\":\"OK\"}"),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
    );
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_lambdaException_propagated() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    when(lambdaClient.invoke(any(InvokeRequest.class)))
        .thenThrow(LambdaException.builder().message("AWS error").build());

    assertThatThrownBy(() -> client.mdnValidate(new MdnValidateRequest(null, null, null, null, null)))
        .isInstanceOf(LambdaException.class);

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_functionError_throws_runtime() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .functionError("Unhandled")
        .payload(SdkBytes.fromString("{}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    assertThatThrownBy(() -> client.otpRequest(new OtpRequest(null, null, null)))
        .isInstanceOf(RuntimeException.class);

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_emptyPayload_throws_runtime() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    assertThatThrownBy(() -> client.otpValidate(new OtpValidateRequest(null, null, null, null)))
        .isInstanceOf(RuntimeException.class);

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_parseError_throws_runtime() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("{\"bad\":true}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    when(objectMapper.readValue(
        eq("{\"bad\":true}"),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<OtpRequestData>>>any()
    )).thenThrow(new IllegalArgumentException("boom"));

    assertThatThrownBy(() -> client.otpRequest(new OtpRequest(null, null, null)))
        .isInstanceOf(RuntimeException.class);

    verify(objectMapper).writeValueAsString(any());
    verify(lambdaClient).invoke(any(InvokeRequest.class));
    verify(objectMapper).readValue(
        eq("{\"bad\":true}"),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<OtpRequestData>>>any()
    );
    verifyNoMoreInteractions(lambdaClient, objectMapper);
  }

  @Test
  void invoke_buildPayloadJson_fails_throws_runtime_and_never_calls_lambda() throws Exception {
    when(objectMapper.writeValueAsString(any()))
        .thenThrow(new IllegalStateException("serialize fail"));

    assertThatThrownBy(() -> client.mdnValidate(new MdnValidateRequest(null, null, null, null, null)))
        .isInstanceOf(RuntimeException.class);

    verify(objectMapper).writeValueAsString(any());
    verifyNoInteractions(lambdaClient);
    verifyNoMoreInteractions(objectMapper);
  }

  @Test
  void invoke_builds_invokeRequest_with_expected_functionName() throws Exception {
    when(objectMapper.writeValueAsString(any())).thenReturn("{}");

    InvokeResponse response = InvokeResponse.builder()
        .statusCode(200)
        .payload(SdkBytes.fromString("{\"status\":\"OK\"}", StandardCharsets.UTF_8))
        .build();

    when(lambdaClient.invoke(any(InvokeRequest.class))).thenReturn(response);

    ApiResponse<MdnValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(objectMapper.readValue(
        eq("{\"status\":\"OK\"}"),
        org.mockito.ArgumentMatchers.<TypeReference<ApiResponse<MdnValidateData>>>any()
    )).thenReturn(expected);

    client.mdnValidate(new MdnValidateRequest(null, null, null, null, null));

    ArgumentCaptor<InvokeRequest> captor = ArgumentCaptor.forClass(InvokeRequest.class);
    verify(lambdaClient).invoke(captor.capture());

    assertThat(captor.getValue().functionName()).isEqualTo(FUNCTION_ARN);
  }
}
