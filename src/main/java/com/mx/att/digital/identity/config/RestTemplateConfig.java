package com.mx.att.digital.identity.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class RestTemplateConfig {

    @Bean(name = "orchestratorRestTemplate")
    public RestTemplate orchestratorRestTemplate(
            RestTemplateBuilder builder,
            @Value("${orchestrator.base-url}") String baseUrl,
            @Value("${orchestrator.timeouts.connect-ms}") int connectMs,
            @Value("${orchestrator.timeouts.read-ms}") int readMs,
            @Value("${orchestrator.security.type}") String secType,
            @Value("${orchestrator.security.basic.username:}") String user,
            @Value("${orchestrator.security.basic.password:}") String pass,
            @Value("${orchestrator.security.bearer.token:}") String token,
            @Value("${ssl.truststore.path:}") String tsPath,
            @Value("${ssl.truststore.password:}") String tsPass,
            @Value("${ssl.truststore.type:JKS}") String tsType,
            @Value("${ssl.insecure-allow-all:false}") boolean insecure,
            @Value("#{${orchestrator.headers:{}}}") Map<String, String> defaultHeaders
    ) throws RestTemplateConfigurationException {
        try {
            SSLContext sslContext;
            HostnameVerifier hostnameVerifier;

            if (insecure) {
                sslContext = SSLContexts.custom()
                        .loadTrustMaterial(null, (chain, authType) -> true)
                        .build();
                hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            } else if (StringUtils.hasText(tsPath)) {
                sslContext = buildTrustStore(tsPath, tsPass, tsType);
                hostnameVerifier = new DefaultHostnameVerifier();
            } else {
                sslContext = SSLContexts.createDefault();
                hostnameVerifier = new DefaultHostnameVerifier();
            }

            HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(
                            SSLConnectionSocketFactoryBuilder.create()
                                    .setSslContext(sslContext)
                                    .setHostnameVerifier(hostnameVerifier)
                                    .build()
                    )
                    .setDefaultSocketConfig(SocketConfig.custom()
                            .setSoTimeout(Timeout.ofMilliseconds(readMs))
                            .build())
                    .build();

            HttpClient httpClient = HttpClientBuilder.create()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    .build();

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
            factory.setConnectTimeout(connectMs);
            factory.setReadTimeout(readMs);

            List<ClientHttpRequestInterceptor> interceptors = buildInterceptors(secType, user, pass, token, defaultHeaders);

            RestTemplate restTemplate = builder
                    .requestFactory(() -> factory)
                    .rootUri(baseUrl)
                    .build();

            restTemplate.getInterceptors().addAll(interceptors);
            return restTemplate;

        } catch (Exception ex) {
            throw new RestTemplateConfigurationException("Failed to configure Orchestrator RestTemplate", ex);
        }
    }

    private SSLContext buildTrustStore(String path, String password, String type) throws RestTemplateConfigurationException {
        try (InputStream is = openResource(path)) {
            KeyStore trustStore = KeyStore.getInstance(type);
            trustStore.load(is, password != null ? password.toCharArray() : null);
            return SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null)
                    .build();
        } catch (Exception ex) {
            throw new RestTemplateConfigurationException("Failed to build SSL TrustStore", ex);
        }
    }

    private List<ClientHttpRequestInterceptor> buildInterceptors(String secType, String user, String pass, String token,
                                                                 Map<String, String> defaultHeaders) {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

        if ("basic".equalsIgnoreCase(secType) && StringUtils.hasText(user)) {
            interceptors.add((req, body, exec) -> {
                req.getHeaders().setBasicAuth(user, pass);
                return exec.execute(req, body);
            });
        } else if ("bearer".equalsIgnoreCase(secType) && StringUtils.hasText(token)) {
            interceptors.add((req, body, exec) -> {
                req.getHeaders().setBearerAuth(token);
                return exec.execute(req, body);
            });
        }

        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            interceptors.add((req, body, exec) -> {
                defaultHeaders.forEach((k, v) -> {
                    if (StringUtils.hasText(k) && StringUtils.hasText(v) && !req.getHeaders().containsKey(k)) {
                        req.getHeaders().add(k, v);
                    }
                });
                return exec.execute(req, body);
            });
        }

        return interceptors;
    }

    private InputStream openResource(String location) throws RestTemplateConfigurationException {
        try {
            if (location.startsWith("classpath:")) {
                String path = location.substring("classpath:".length());
                InputStream is = getClass().getResourceAsStream(path.startsWith("/") ? path : "/" + path);
                if (is == null)
                    throw new RestTemplateConfigurationException("Truststore not found: " + location);
                return is;
            }
            if (location.startsWith("file:")) {
                return URI.create(location).toURL().openStream();
            }
            return new java.io.FileInputStream(location);
        } catch (Exception ex) {
            throw new RestTemplateConfigurationException("Failed to open resource: " + location, ex);
        }
    }

    public static class RestTemplateConfigurationException extends Exception {
        public RestTemplateConfigurationException(String message) { super(message); }
        public RestTemplateConfigurationException(String message, Throwable cause) { super(message, cause); }
    }
}
