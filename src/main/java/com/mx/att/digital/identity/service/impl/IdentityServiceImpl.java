package com.mx.att.digital.identity.service.impl;

import com.mx.att.digital.identity.client.OrchestratorClient;
import com.mx.att.digital.identity.model.*;
import com.mx.att.digital.identity.service.IdentityService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    public ApiResponse<SessionInitData> sessionInit(SessionInitRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] sessionInit uuid={}", safe(req == null ? null : req.uuid()));
        }
        return client.sessionInit(req);
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<MdnValidateData> mdnValidate(MdnValidateRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] mdnValidate uuid={} msisdn={}",
                safe(req == null ? null : req.uuid()),
                safe(req == null ? null : req.msisdn())
            );
        }
        return client.mdnValidate(req);
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<OtpRequestData> otpRequest(OtpRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] otpRequest uuid={}", safe(req == null ? null : req.uuid()));
        }
        return client.otpRequest(req);
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<OtpValidateData> otpValidate(OtpValidateRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] otpValidate uuid={}", safe(req == null ? null : req.uuid()));
        }
        return client.otpValidate(req);
    }

    @Override
    @CircuitBreaker(name = "orchestrator")
    @Retry(name = "orchestrator")
    public ApiResponse<OtpForwardData> otpForward(OtpForwardRequest req) {
        if (log.isInfoEnabled()) {
            log.info("[IdentityService] otpForward uuid={}", safe(req == null ? null : req.uuid()));
        }
        return client.otpForward(req);
    }

    private String safe(String v) {
        return Objects.toString(v, "-");
    }
}
