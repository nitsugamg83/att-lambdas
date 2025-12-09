package com.mx.att.digital.identity.controller;

import jakarta.validation.Valid;
import com.mx.att.digital.identity.model.*;
import com.mx.att.digital.identity.service.IdentityService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class IdentityController {

  private final IdentityService service;
  public IdentityController(IdentityService service) { this.service = service; }

  @PostMapping(path = "/session/init", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<SessionInitData>> sessionInit(@Valid @RequestBody SessionInitRequest req) {
    return ResponseEntity.ok(service.sessionInit(req));
  }

  @PostMapping(path = "/mdn/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<MdnValidateData>> mdnValidate(@Valid @RequestBody MdnValidateRequest req) {
    return ResponseEntity.ok(service.mdnValidate(req));
  }

  @PostMapping(path = "/otp/request", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpRequestData>> otpRequest(@Valid @RequestBody OtpRequest req) {
    return ResponseEntity.ok(service.otpRequest(req));
  }

  @PostMapping(path = "/otp/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpValidateData>> otpValidate(@Valid @RequestBody OtpValidateRequest req) {
    return ResponseEntity.ok(service.otpValidate(req));
  }

  @PostMapping(path = "/otp/forward", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OtpForwardData>> otpForward(@Valid @RequestBody OtpForwardRequest req) {
    return ResponseEntity.ok(service.otpForward(req));
  }
}
