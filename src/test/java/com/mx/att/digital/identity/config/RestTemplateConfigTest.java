package com.mx.att.digital.identity.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

  private final RestTemplateConfig config = new RestTemplateConfig();

  @Test
  void restTemplate_created_insecure_true_and_basic_auth_interceptor_sets_header() throws Exception {
    RestTemplate rest = config.orchestratorRestTemplate(
        new RestTemplateBuilder(),
        "http://localhost",
        1000,
        1000,
        "basic",
        "user",
        "pass",
        "",
        "",
        "",
        "JKS",
        true, // insecure -> rama 1
        Map.of()
    );

    assertThat(rest).isNotNull();
    assertThat(rest.getInterceptors()).isNotEmpty();

    // Ejecuta el interceptor y valida que mete BasicAuth
    ClientHttpRequestInterceptor authInterceptor = rest.getInterceptors().get(0);

    HttpHeaders headers = new HttpHeaders();
    HttpRequest req = request("http://localhost/test", headers);

    MockClientHttpResponse resp = (MockClientHttpResponse) authInterceptor.intercept(
        req,
        new byte[0],
        okExecution()
    );

    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).startsWith("Basic ");
  }

  @Test
  void restTemplate_bearer_auth_interceptor_sets_header() throws Exception {
    RestTemplate rest = config.orchestratorRestTemplate(
        new RestTemplateBuilder(),
        "http://localhost",
        1000,
        1000,
        "bearer",
        "",
        "",
        "token-123",
        "",
        "",
        "JKS",
        true, // no importa aquí, solo queremos interceptor bearer
        Map.of()
    );

    assertThat(rest.getInterceptors()).isNotEmpty();

    ClientHttpRequestInterceptor interceptor = rest.getInterceptors().get(0);

    HttpHeaders headers = new HttpHeaders();
    HttpRequest req = request("http://localhost/test", headers);

    MockClientHttpResponse resp = (MockClientHttpResponse) interceptor.intercept(
        req,
        new byte[0],
        okExecution()
    );

    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer token-123");
  }

  @Test
  void restTemplate_default_headers_interceptor_adds_only_when_absent_and_skips_blank() throws Exception {
    Map<String, String> defaults = new LinkedHashMap<>();
    defaults.put("X-Trace", "abc");
    defaults.put("X-Existing", "should-not-override");
    defaults.put("", "ignored");
    defaults.put("X-BlankValue", "");
    defaults.put("X-NullLike", null); // será tratado como no hasText

    RestTemplate rest = config.orchestratorRestTemplate(
        new RestTemplateBuilder(),
        "http://localhost",
        1000,
        1000,
        "basic",
        "user",
        "pass",
        "",
        "",
        "",
        "JKS",
        true,
        defaults
    );

    // basic interceptor + headers interceptor
    assertThat(rest.getInterceptors())
    .hasSizeGreaterThanOrEqualTo(2);


    // el último debe ser el de default headers (según el orden del config)
    ClientHttpRequestInterceptor headersInterceptor = rest.getInterceptors().get(rest.getInterceptors().size() - 1);

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Existing", "present"); // ya existe => no se debe sobreescribir

    HttpRequest req = request("http://localhost/test", headers);

    MockClientHttpResponse resp = (MockClientHttpResponse) headersInterceptor.intercept(
        req,
        new byte[0],
        okExecution()
    );

    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertThat(headers.getFirst("X-Trace")).isEqualTo("abc");
    assertThat(headers.getFirst("X-Existing")).isEqualTo("present");
    assertThat(headers.containsKey("X-BlankValue")).isFalse();
  }

    @Test
    void restTemplate_truststore_branch_uses_file_uri_openResource() throws Exception {
    File ksFile = File.createTempFile("test-truststore", ".jks");
    ksFile.deleteOnExit();

    char[] password = "changeit".toCharArray();
    KeyStore ks = KeyStore.getInstance("JKS");
    ks.load(null, password);
    try (FileOutputStream fos = new FileOutputStream(ksFile)) {
        ks.store(fos, password);
    }

    // IMPORTANTE en Windows: usar el URI tal cual ("file:/C:/...")
    String tsPath = ksFile.toURI().toString();

    RestTemplate rest = config.orchestratorRestTemplate(
        new RestTemplateBuilder(),
        "http://localhost",
        1000,
        1000,
        "basic",
        "user",
        "pass",
        "",
        tsPath,
        String.valueOf(password),
        "JKS",
        false,
        Map.of()
    );

    assertThat(rest).isNotNull();
    assertThat(rest.getInterceptors()).isNotEmpty();
    }

  @Test
  void restTemplate_default_jvm_trust_branch_when_no_insecure_and_no_truststore() throws Exception {
    // insecure false y tsPath vacío => rama SSLContexts.createDefault()
    RestTemplate rest = config.orchestratorRestTemplate(
        new RestTemplateBuilder(),
        "http://localhost",
        1000,
        1000,
        "basic",
        "user",
        "pass",
        "",
        "",     // tsPath vacío
        "",     // tsPass
        "JKS",
        false,  // insecure false
        Map.of()
    );

    assertThat(rest).isNotNull();
    assertThat(rest.getInterceptors()).isNotEmpty();
  }

  private static HttpRequest request(String url, HttpHeaders headers) {
  return new HttpRequest() {
    @Override public HttpHeaders getHeaders() { return headers; }
    @Override public HttpMethod getMethod() { return HttpMethod.GET; }
    @Override public URI getURI() { return URI.create(url); }
  };
}


  private static ClientHttpRequestExecution okExecution() {
    return (req, body) -> new MockClientHttpResponse(new byte[0], HttpStatus.OK);
  }
}
