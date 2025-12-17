package com.mx.att.digital.identity.service.impl;

import com.mx.att.digital.identity.client.OrchestratorClient;
import com.mx.att.digital.identity.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IdentityServiceImplTest {

  private OrchestratorClient client;
  private IdentityServiceImpl service;

  @BeforeEach
  void setUp() {
    client = mock(OrchestratorClient.class);
    service = new IdentityServiceImpl(client);
  }

  @Test
  void sessionInit_delegates_to_client_with_req() {
    SessionInitRequest req = new SessionInitRequest(null, null, null);
    ApiResponse<SessionInitData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.sessionInit(req)).thenReturn(expected);

    ApiResponse<SessionInitData> out = service.sessionInit(req);

    assertThat(out).isSameAs(expected);
    verify(client).sessionInit(req);
    verifyNoMoreInteractions(client);
  }

  @Test
  void sessionInit_delegates_to_client_with_null_req() {
    ApiResponse<SessionInitData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.sessionInit(null)).thenReturn(expected);

    ApiResponse<SessionInitData> out = service.sessionInit(null);

    assertThat(out).isSameAs(expected);
    verify(client).sessionInit(null);
    verifyNoMoreInteractions(client);
  }

  @Test
  void mdnValidate_delegates_to_client_with_req() {
    MdnValidateRequest req = new MdnValidateRequest(null, null, null, null, null);
    ApiResponse<MdnValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.mdnValidate(req)).thenReturn(expected);

    ApiResponse<MdnValidateData> out = service.mdnValidate(req);

    assertThat(out).isSameAs(expected);
    verify(client).mdnValidate(req);
    verifyNoMoreInteractions(client);
  }

  @Test
  void mdnValidate_delegates_to_client_with_null_req() {
    ApiResponse<MdnValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.mdnValidate(null)).thenReturn(expected);

    ApiResponse<MdnValidateData> out = service.mdnValidate(null);

    assertThat(out).isSameAs(expected);
    verify(client).mdnValidate(null);
    verifyNoMoreInteractions(client);
  }

  @Test
  void otpRequest_delegates_to_client_with_req() {
    OtpRequest req = new OtpRequest(null, null, null);
    ApiResponse<OtpRequestData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.otpRequest(req)).thenReturn(expected);

    ApiResponse<OtpRequestData> out = service.otpRequest(req);

    assertThat(out).isSameAs(expected);
    verify(client).otpRequest(req);
    verifyNoMoreInteractions(client);
  }

  @Test
  void otpRequest_delegates_to_client_with_null_req() {
    ApiResponse<OtpRequestData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.otpRequest(null)).thenReturn(expected);

    ApiResponse<OtpRequestData> out = service.otpRequest(null);

    assertThat(out).isSameAs(expected);
    verify(client).otpRequest(null);
    verifyNoMoreInteractions(client);
  }

  @Test
  void otpValidate_delegates_to_client_with_req() {
    OtpValidateRequest req = new OtpValidateRequest(null, null, null, null);
    ApiResponse<OtpValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.otpValidate(req)).thenReturn(expected);

    ApiResponse<OtpValidateData> out = service.otpValidate(req);

    assertThat(out).isSameAs(expected);
    verify(client).otpValidate(req);
    verifyNoMoreInteractions(client);
  }

  @Test
  void otpValidate_delegates_to_client_with_null_req() {
    ApiResponse<OtpValidateData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.otpValidate(null)).thenReturn(expected);

    ApiResponse<OtpValidateData> out = service.otpValidate(null);

    assertThat(out).isSameAs(expected);
    verify(client).otpValidate(null);
    verifyNoMoreInteractions(client);
  }

  @Test
  void otpForward_delegates_to_client_with_null_req() {
    ApiResponse<OtpForwardData> expected =
        new ApiResponse<>("OK", "msg", null, OffsetDateTime.now());

    when(client.otpForward(null)).thenReturn(expected);

    ApiResponse<OtpForwardData> out = service.otpForward(null);

    assertThat(out).isSameAs(expected);
    verify(client).otpForward(null);
    verifyNoMoreInteractions(client);
  }
}
