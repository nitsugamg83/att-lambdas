package com.mx.att.digital.identity.service.impl;

import com.mx.att.digital.identity.client.OrchestratorClient;
import com.mx.att.digital.identity.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IdentityServiceImplTest {

  @Mock
  private OrchestratorClient client;

  private IdentityServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new IdentityServiceImpl(client);
  }

  private static OffsetDateTime ts() {
    return OffsetDateTime.parse("2025-01-01T10:00:00Z");
  }

  @Nested
  @DisplayName("sessionInit")
  class SessionInitTests {

    @Test
    @DisplayName("delegates to client.sessionInit and returns same response")
    void sessionInit_delegates_and_returns() {
      SessionInitRequest req = new SessionInitRequest("uuid-1", ts(), "1234567890");

      SessionInitData data = new SessionInitData("uuid-1", "0", "OK");
      ApiResponse<SessionInitData> expected = new ApiResponse<>("OK", "ok", data, ts());

      when(client.sessionInit(req)).thenReturn(expected);

      ApiResponse<SessionInitData> actual = service.sessionInit(req);

      assertThat(actual).isSameAs(expected);
      verify(client, times(1)).sessionInit(req);
      verifyNoMoreInteractions(client);
    }
  }

  @Nested
  @DisplayName("mdnValidate")
  class MdnValidateTests {

    @Test
    @DisplayName("delegates to client.mdnValidate and returns same response")
    void mdnValidate_delegates_and_returns() {
      MdnValidateRequest req = new MdnValidateRequest("uuid-1", ts(), "src", "flow", "1234567890");

      MdnValidateData data = new MdnValidateData(
          "uuid-1", "0", "OK", "1234567890", "PREPAID", "matrixx", true, "ACTIVE"
      );
      ApiResponse<MdnValidateData> expected = new ApiResponse<>("OK", "ok", data, ts());

      when(client.mdnValidate(req)).thenReturn(expected);

      ApiResponse<MdnValidateData> actual = service.mdnValidate(req);

      assertThat(actual).isSameAs(expected);
      verify(client, times(1)).mdnValidate(req);
      verifyNoMoreInteractions(client);
    }
  }

  @Nested
  @DisplayName("otpRequest")
  class OtpRequestTests {

    @Test
    @DisplayName("delegates to client.otpRequest and returns same response")
    void otpRequest_delegates_and_returns() {
      OtpRequest req = new OtpRequest("uuid-1", "1234567890", ts());

      OtpRequestData data = new OtpRequestData("uuid-1", "0", "OK");
      ApiResponse<OtpRequestData> expected = new ApiResponse<>("OK", "ok", data, ts());

      when(client.otpRequest(req)).thenReturn(expected);

      ApiResponse<OtpRequestData> actual = service.otpRequest(req);

      assertThat(actual).isSameAs(expected);
      verify(client, times(1)).otpRequest(req);
      verifyNoMoreInteractions(client);
    }
  }

  @Nested
  @DisplayName("otpValidate")
  class OtpValidateTests {

    @Test
    @DisplayName("delegates to client.otpValidate and returns same response")
    void otpValidate_delegates_and_returns() {
      OtpValidateRequest req = new OtpValidateRequest("uuid-1", ts(), "1234567890", "123456");

      OtpValidateData data = new OtpValidateData("uuid-1", "0", "OK", "https://example.com/onboarding");
      ApiResponse<OtpValidateData> expected = new ApiResponse<>("OK", "ok", data, ts());

      when(client.otpValidate(req)).thenReturn(expected);

      ApiResponse<OtpValidateData> actual = service.otpValidate(req);

      assertThat(actual).isSameAs(expected);
      verify(client, times(1)).otpValidate(req);
      verifyNoMoreInteractions(client);
    }
  }

  @Nested
  @DisplayName("otpForward")
  class OtpForwardTests {

    @Test
    @DisplayName("delegates to client.otpForward and returns same response")
    void otpForward_delegates_and_returns() {
      OtpForwardRequest req = new OtpForwardRequest("uuid-1", ts(), "1234567890");

      OtpForwardData data = new OtpForwardData("uuid-1", "0", "OK");
      ApiResponse<OtpForwardData> expected = new ApiResponse<>("OK", "ok", data, ts());

      when(client.otpForward(req)).thenReturn(expected);

      ApiResponse<OtpForwardData> actual = service.otpForward(req);

      assertThat(actual).isSameAs(expected);
      verify(client, times(1)).otpForward(req);
      verifyNoMoreInteractions(client);
    }
  }
}
