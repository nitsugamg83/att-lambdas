package com.mx.att.digital.identity.exception;

import com.mx.att.digital.identity.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* ===================== 404 de recursos estáticos (favicon, etc.) ===================== */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
        // No “ensuciamos” logs con ERROR por favicon
        String msg = ex.getMessage();
        if (msg != null && msg.contains("favicon.ico")) {
            log.debug("Static resource missing: {}", msg);
        } else {
            log.info("Resource not found: {}", msg);
        }
        return build(HttpStatus.NOT_FOUND, "Resource not found", false, "RESOURCE_NOT_FOUND");
    }

    /* ===================== Validación @Valid / Bean Validation ===================== */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        String msg = errors.isEmpty() ? "Validation failed" : String.join("; ", errors);
        log.debug("Validation error: {}", msg);
        return build(HttpStatus.BAD_REQUEST, msg, false, "VALIDATION_ERROR");
    }

    private String formatFieldError(FieldError fe) {
        String field = fe.getField();
        String code = fe.getCode();
        Object rejected = fe.getRejectedValue();
        String defMsg = fe.getDefaultMessage();
        return String.format("%s: %s (rejected=%s, code=%s)", field, defMsg, rejected, code);
    }

    /* ===================== Request mal formados (JSON inválido, tipos, etc.) ===================== */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        log.debug("Malformed JSON: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", false, "MALFORMED_JSON");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String msg = "Missing request parameter: " + ex.getParameterName();
        log.debug(msg);
        return build(HttpStatus.BAD_REQUEST, msg, false, "MISSING_PARAMETER");
    }

    /* ===================== Errores propagados de clientes HTTP (RestTemplate) ===================== */
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorResponse> handleHttpClient(HttpStatusCodeException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());

        String body = ex.getResponseBodyAsString();
        String detail = body.isBlank() ? ex.getMessage() : body;
        String msg = "Upstream error (" + ex.getStatusCode().value() + "): " + detail;

        boolean retryable = ex.getStatusCode().is5xxServerError();
        log.warn("HTTP client error. status={}, retryable={}, body={}", ex.getStatusCode(), retryable, body);

        return build(status != null ? status : HttpStatus.BAD_GATEWAY,
                msg,
                retryable,
                ex.getStatusCode().is4xxClientError() ? "UPSTREAM_4XX" : "UPSTREAM_5XX");
    }

    /* ===================== Fallback genérico ===================== */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", true, "UNEXPECTED_ERROR");
    }

    /* ===================== Helper ===================== */
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, boolean retryable, String code) {
        ErrorResponse body = new ErrorResponse(
                status.name(),
                message,
                retryable,
                code,
                OffsetDateTime.now()
        );
        return ResponseEntity.status(status).body(body);
    }
}
