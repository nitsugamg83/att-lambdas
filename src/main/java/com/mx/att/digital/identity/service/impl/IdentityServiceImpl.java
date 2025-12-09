package com.mx.att.digital.identity.service.impl;

import com.mx.att.digital.identity.client.OrchestratorClient;
import com.mx.att.digital.identity.model.*;
import com.mx.att.digital.identity.service.IdentityService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IdentityServiceImpl implements IdentityService {

    private static final Logger log = LoggerFactory.getLogger(IdentityServiceImpl.class);

    private final OrchestratorClient client;

    public IdentityServiceImpl(OrchestratorClient client) {
        this.client = client;
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    @TimeLimiter(name = "orchestrator")
    public ApiResponse<SessionInitData> sessionInit(SessionInitRequest req) {
        log.info("[IdentityService] sessionInit uuid={}", req.uuid());
        return client.sessionInit(req);
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    @TimeLimiter(name = "orchestrator")
    public ApiResponse<MdnValidateData> mdnValidate(MdnValidateRequest req) {
        log.info("[IdentityService] mdnValidate uuid={}", req.uuid());
        return client.mdnValidate(req);
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    @TimeLimiter(name = "orchestrator")
    public ApiResponse<OtpRequestData> otpRequest(OtpRequest req) {
        log.info("[IdentityService] otpRequest uuid={}", req.uuid());
        return client.otpRequest(req);
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    @TimeLimiter(name = "orchestrator")
    public ApiResponse<OtpValidateData> otpValidate(OtpValidateRequest req) {
        log.info("[IdentityService] otpValidate uuid={}", req.uuid());
        return client.otpValidate(req);
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    @TimeLimiter(name = "orchestrator")
    public ApiResponse<OtpForwardData> otpForward(OtpForwardRequest req) {
        log.info("[IdentityService] otpForward uuid={}", req.uuid());
        return client.otpForward(req);
    }
}
