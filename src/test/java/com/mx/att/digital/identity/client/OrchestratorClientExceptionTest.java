package com.mx.att.digital.identity.client;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class OrchestratorClientExceptionTest {

  @Test
  void ctor_message_setsMessage_andNoCause() {
    OrchestratorClientException ex = new OrchestratorClientException("boom");

    assertThat(ex.getMessage()).isEqualTo("boom");
    assertThat(ex.getCause()).isNull();
  }

  @Test
  void ctor_messageAndCause_setsBoth() {
    Throwable cause = new IllegalStateException("root");

    OrchestratorClientException ex = new OrchestratorClientException("boom", cause);

    assertThat(ex.getMessage()).isEqualTo("boom");
    assertThat(ex.getCause()).isSameAs(cause);
  }
}
