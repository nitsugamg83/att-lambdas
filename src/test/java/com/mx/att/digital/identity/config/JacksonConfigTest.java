package com.mx.att.digital.identity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mx.att.digital.identity.config.JacksonConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        JacksonConfig.class,
        JacksonConfigTest.TestConfig.class
})
class JacksonConfigTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void object_mapper_created() {
        assertThat(objectMapper).isNotNull();
    }

    @Test
    void java_time_is_serialized_as_iso_string() throws Exception {
        OffsetDateTime now = OffsetDateTime.parse("2024-01-01T10:15:30+01:00");

        String json = objectMapper.writeValueAsString(now);

        assertThat(json).contains("2024-01-01T10:15:30");
    }

    @Test
    void timestamps_disabled() {
        assertThat(
                objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        ).isFalse();
    }

    @Configuration
    static class TestConfig {

        @Bean
        Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
            return Jackson2ObjectMapperBuilder.json();
        }
    }
}



