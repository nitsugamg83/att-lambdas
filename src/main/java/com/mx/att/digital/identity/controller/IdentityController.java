package com.mx.att.digital.identity.controller;

import jakarta.validation.Valid;

import com.mx.att.digital.identity.model.ApiResponse;
import com.mx.att.digital.identity.model.MdnValidateData;
import com.mx.att.digital.identity.model.MdnValidateRequest;
import com.mx.att.digital.identity.model.OtpForwardData;
import com.mx.att.digital.identity.model.OtpForwardRequest;
import com.mx.att.digital.identity.model.OtpRequest;
import com.mx.att.digital.identity.model.OtpRequestData;
import com.mx.att.digital.identity.model.OtpValidateData;
import com.mx.att.digital.identity.model.OtpValidateRequest;
import com.mx.att.digital.identity.model.SessionInitData;
import com.mx.att.digital.identity.model.SessionInitRequest;
import com.mx.att.digital.identity.service.IdentityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Identity Orchestration", description = "Endpoints para orquestación de identidad (AT&T MX)")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityController {

  private final IdentityService service;
  public IdentityController(IdentityService service) { this.service = service; }

  @Operation(
      summary = "Inicializa sesión",
      description = "Crea una sesión de orquestación (por ejemplo, para flujos de verificación).",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Sesión creada",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = SessionInitRequest.class))
  )
  @PostMapping(path = "/session/init", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<SessionInitData>> sessionInit(@Valid @RequestBody SessionInitRequest req) {
    return ResponseEntity.ok(service.sessionInit(req));
  }

  @Operation(
      summary = "Valida MDN",
      description = "Valida MSISDN/MDN dentro del flujo de identidad.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "Validación procesada",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = MdnValidateRequest.class))
  )
  @PostMapping(path = "/mdn/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<MdnValidateData>> mdnValidate(@Valid @RequestBody MdnValidateRequest req) {
    return ResponseEntity.ok(service.mdnValidate(req));
  }

  @Operation(
      summary = "Solicita OTP",
      description = "Genera y envía un OTP al canal configurado.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "OTP solicitado",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = OtpRequest.class))
  )
  @PostMapping(path = "/otp/request", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpRequestData>> otpRequest(@Valid @RequestBody OtpRequest req) {
    return ResponseEntity.ok(service.otpRequest(req));
  }

  @Operation(
      summary = "Valida OTP",
      description = "Valida el código OTP recibido por el usuario.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "OTP validado",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = OtpValidateRequest.class))
  )
  @PostMapping(path = "/otp/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpValidateData>> otpValidate(@Valid @RequestBody OtpValidateRequest req) {
    return ResponseEntity.ok(service.otpValidate(req));
  }

  @Operation(
      summary = "Reenvía OTP",
      description = "Reenvía el OTP al usuario por el canal configurado.",
      responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
              responseCode = "200",
              description = "OTP reenviado",
              content = @Content(schema = @Schema(implementation = ApiResponse.class))
          ),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado"),
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
      }
  )
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      content = @Content(schema = @Schema(implementation = OtpForwardRequest.class))
  )
  @PostMapping(path = "/otp/forward", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpForwardData>> otpForward(@Valid @RequestBody OtpForwardRequest req) {
    return ResponseEntity.ok(service.otpForward(req));
  }
}
