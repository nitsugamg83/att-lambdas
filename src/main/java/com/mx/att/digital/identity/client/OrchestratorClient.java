package com.mx.att.digital.identity.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.att.digital.identity.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.LogType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class OrchestratorClient {

  private static final Logger log = LoggerFactory.getLogger(OrchestratorClient.class);

  private final LambdaClient lambda;
  private final ObjectMapper mapper;
  private final String functionArn;
  private final String invocationType; // RequestResponse | Event
  private final String logType;        // None | Tail

  public OrchestratorClient(
      LambdaClient lambda,
      ObjectMapper mapper,
      @Value("${aws.lambda.function-arn}") String functionArn,
      @Value("${aws.lambda.invocation-type:RequestResponse}") String invocationType,
      @Value("${aws.lambda.log-type:Tail}") String logType
  ) {
    this.lambda = lambda;
    this.mapper = mapper;
    this.functionArn = Objects.requireNonNull(functionArn, "aws.lambda.function-arn es requerido");
    this.invocationType = invocationType;
    this.logType = logType;
  }

  // ===== Métodos públicos (MISMAS FIRMAS) =====

  public ApiResponse<SessionInitData> sessionInit(SessionInitRequest req) {
    return invoke("sessionInit", req, new TypeReference<>() {});
  }

  public ApiResponse<MdnValidateData> mdnValidate(MdnValidateRequest req) {
    return invoke("mdnValidate", req, new TypeReference<>() {});
  }

  public ApiResponse<OtpRequestData> otpRequest(OtpRequest req) {
    return invoke("otpRequest", req, new TypeReference<>() {});
  }

  public ApiResponse<OtpValidateData> otpValidate(OtpValidateRequest req) {
    return invoke("otpValidate", req, new TypeReference<>() {});
  }

  public ApiResponse<OtpForwardData> otpForward(OtpForwardRequest req) {
    return invoke("otpForward", req, new TypeReference<>() {});
  }

  // ===== Implementación interna con AWS Lambda =====

  private <TReq, TRes> ApiResponse<TRes> invoke(String operation, TReq requestBody,
                                                TypeReference<ApiResponse<TRes>> typeRef) {
    try {
      // Construimos el payload esperado por tu Lambda:
      // {
      //   "operation": "<operation>",
      //   "request":   <requestBody JSON>
      // }
      Map<String, Object> payload = new HashMap<>();
      payload.put("operation", operation);
      payload.put("request", requestBody);

      String json = mapper.writeValueAsString(payload);

      InvokeRequest invokeReq = InvokeRequest.builder()
          .functionName(functionArn)
          .invocationType(invocationType) // "RequestResponse" o "Event"
          .logType(resolveLogType(logType))
          .payload(SdkBytes.fromString(json, StandardCharsets.UTF_8))
          .build();

      log.debug("[LAMBDA] invoke op={} arn={} itype={} log={}", operation, functionArn, invocationType, logType);

      InvokeResponse resp = lambda.invoke(invokeReq);

      // Log del runtime de Lambda (si Tail)
      if (resp.logResult() != null && !resp.logResult().isEmpty()) {
        String logsDecoded = new String(Base64.getDecoder().decode(resp.logResult()), StandardCharsets.UTF_8);
        log.debug("[LAMBDA][logs]\n{}", logsDecoded);
      }

      if (resp.functionError() != null && !resp.functionError().isEmpty()) {
        // La invocación llegó, pero la función lanzó error
        String errPayload = (resp.payload() != null) ? resp.payload().asUtf8String() : "";
        log.error("[LAMBDA] functionError={} statusCode={} payload={}", resp.functionError(), resp.statusCode(), errPayload);
        throw new RuntimeException("Lambda function error: " + resp.functionError());
      }

      String body = (resp.payload() != null) ? resp.payload().asUtf8String() : null;
      if (body == null || body.isBlank()) {
        log.error("[LAMBDA] Respuesta vacía de Lambda (statusCode={})", resp.statusCode());
        throw new RuntimeException("Respuesta vacía de Lambda");
      }

      ApiResponse<TRes> parsed = mapper.readValue(body, typeRef);
      if (parsed == null) {
        throw new RuntimeException("No se pudo parsear la respuesta de Lambda");
      }

      return parsed;
    } catch (LambdaException awsEx) {
      log.error("[LAMBDA] Error AWS invoking function arn={} op={} code={} msg={}",
          functionArn, operation,
          awsEx.awsErrorDetails() != null ? awsEx.awsErrorDetails().errorCode() : "n/a",
          awsEx.awsErrorDetails() != null ? awsEx.awsErrorDetails().errorMessage() : awsEx.getMessage(),
          awsEx);
      throw awsEx;
    } catch (Exception ex) {
      log.error("[LAMBDA] Error invocando op={} arn={}: {}", operation, functionArn, ex.getMessage(), ex);
      throw new RuntimeException("Error invocando Lambda: " + ex.getMessage(), ex);
    }
  }

  private LogType resolveLogType(String configured) {
    if ("Tail".equalsIgnoreCase(configured)) return LogType.TAIL;
    return LogType.NONE;
  }
}
