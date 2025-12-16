package com.mx.att.digital.identity.controller;

import com.mx.att.digital.identity.model.*;
import com.mx.att.digital.identity.service.IdentityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias de capa Controller para IdentityController.
 * Usa MockMvc standalone con un Validator "no-op" para evitar 400 por @Valid.
 */
class IdentityControllerTest {

  /** Validator que no valida nada (evita 400 por constraints desconocidos). */
  static class NoOpValidator implements Validator {
    @Override public boolean supports(Class<?> clazz) { return true; }
    @Override public void validate(Object target, Errors errors) { /* no-op */ }
  }

  @Mock
  private IdentityService service;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    IdentityController controller = new IdentityController(service);

    this.mvc = MockMvcBuilders
        .standaloneSetup(controller)
        // Desactiva efectivamente la validación de @Valid para estos tests de routing
        .setValidator(new NoOpValidator())
        .build();
  }

  @Nested
  @DisplayName("POST /session/init")
  class SessionInitEndpoint {
    @Test
    @DisplayName("200 OK e invoca service.sessionInit")
    void sessionInit_ok_invoca_service() throws Exception {
      when(service.sessionInit(ArgumentMatchers.any(SessionInitRequest.class))).thenReturn(null);

      mvc.perform(post("/session/init")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isOk());

      verify(service, times(1)).sessionInit(ArgumentMatchers.any(SessionInitRequest.class));
      verifyNoMoreInteractions(service);
    }
  }

  @Nested
  @DisplayName("POST /mdn/validate")
  class MdnValidateEndpoint {
    @Test
    @DisplayName("200 OK e invoca service.mdnValidate")
    void mdnValidate_ok_invoca_service() throws Exception {
      when(service.mdnValidate(ArgumentMatchers.any(MdnValidateRequest.class))).thenReturn(null);

      mvc.perform(post("/mdn/validate")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isOk());

      verify(service, times(1)).mdnValidate(ArgumentMatchers.any(MdnValidateRequest.class));
      verifyNoMoreInteractions(service);
    }
  }

  @Nested
  @DisplayName("POST /otp/request")
  class OtpRequestEndpoint {
    @Test
    @DisplayName("200 OK e invoca service.otpRequest")
    void otpRequest_ok_invoca_service() throws Exception {
      when(service.otpRequest(ArgumentMatchers.any(OtpRequest.class))).thenReturn(null);

      mvc.perform(post("/otp/request")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isOk());

      verify(service, times(1)).otpRequest(ArgumentMatchers.any(OtpRequest.class));
      verifyNoMoreInteractions(service);
    }
  }

  @Nested
  @DisplayName("POST /otp/validate")
  class OtpValidateEndpoint {
    @Test
    @DisplayName("200 OK e invoca service.otpValidate")
    void otpValidate_ok_invoca_service() throws Exception {
      when(service.otpValidate(ArgumentMatchers.any(OtpValidateRequest.class))).thenReturn(null);

      mvc.perform(post("/otp/validate")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isOk());

      verify(service, times(1)).otpValidate(ArgumentMatchers.any(OtpValidateRequest.class));
      verifyNoMoreInteractions(service);
    }
  }

  @Nested
  @DisplayName("POST /otp/forward")
  class OtpForwardEndpoint {
    @Test
    @DisplayName("200 OK e invoca service.otpForward")
    void otpForward_ok_invoca_service() throws Exception {
      when(service.otpForward(ArgumentMatchers.any(OtpForwardRequest.class))).thenReturn(null);

      mvc.perform(post("/otp/forward")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isOk());

      verify(service, times(1)).otpForward(ArgumentMatchers.any(OtpForwardRequest.class));
      verifyNoMoreInteractions(service);
    }
  }
}
