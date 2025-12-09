package com.mx.att.digital.identity.client;

import com.mx.att.digital.identity.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrchestratorClient {

  private final RestTemplate rest;

  public OrchestratorClient(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                            RestTemplate orchestratorRestTemplate) {
    this.rest = orchestratorRestTemplate;
  }

  public ApiResponse<SessionInitData> sessionInit(SessionInitRequest req) {
    return post("/session/init", req, new ParameterizedTypeReference<>() {});
  }

  public ApiResponse<MdnValidateData> mdnValidate(MdnValidateRequest req) {
    return post("/mdn/validate", req, new ParameterizedTypeReference<>() {});
  }

  public ApiResponse<OtpRequestData> otpRequest(OtpRequest req) {
    return post("/otp/request", req, new ParameterizedTypeReference<>() {});
  }

  public ApiResponse<OtpValidateData> otpValidate(OtpValidateRequest req) {
    return post("/otp/validate", req, new ParameterizedTypeReference<>() {});
  }

  public ApiResponse<OtpForwardData> otpForward(OtpForwardRequest req) {
    return post("/otp/forward", req, new ParameterizedTypeReference<>() {});
  }

  private <TReq, TRes> ApiResponse<TRes> post(String path, TReq body,
                                              ParameterizedTypeReference<ApiResponse<TRes>> type) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<TReq> entity = new HttpEntity<>(body, headers);

    ResponseEntity<ApiResponse<TRes>> resp = rest.exchange(
        path, HttpMethod.POST, entity, type);

    return resp.getBody();
  }
}
