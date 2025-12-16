package com.mx.att.digital.identity.controller.identity.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import com.mx.att.digital.identity.config.RestTemplateConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({RestTemplateConfig.class, RestTemplateConfigTest.TestConfig.class})
@TestPropertySource(properties = {
        "orchestrator.base-url=http://localhost",
        "orchestrator.timeouts.connect-ms=1000",
        "orchestrator.timeouts.read-ms=1000",
        "orchestrator.security.type=basic",
        "orchestrator.security.basic.username=user",
        "orchestrator.security.basic.password=pass",
        "ssl.insecure-allow-all=true"
})
class RestTemplateConfigTest {

    @Autowired
    RestTemplate orchestratorRestTemplate;

    @Test
    void rest_template_created() {
        assertThat(orchestratorRestTemplate).isNotNull();
    }

    @Test
    void interceptors_added() {
        assertThat(orchestratorRestTemplate.getInterceptors()).isNotEmpty();
    }

    /* ===== Builder mínimo ===== */
    @Configuration
    static class TestConfig {
        @Bean
        RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder();
        }
    }
}
