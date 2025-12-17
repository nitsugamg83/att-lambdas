package com.mx.att.digital.identity.client;

public class OrchestratorClientException extends RuntimeException {
  public OrchestratorClientException(String message) {
    super(message);
  }

  public OrchestratorClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
