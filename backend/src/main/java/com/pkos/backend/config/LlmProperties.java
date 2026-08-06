package com.pkos.backend.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

    private String provider = "gemini";

    private Retry retry = new Retry();

    private Fallback fallback = new Fallback();

    @Getter
    @Setter
    public static class Retry {

        private int maxAttempts = 3;

        private Duration delay = Duration.ofMillis(500);

    }

    @Getter
    @Setter
    public static class Fallback {

        private boolean enabled = true;

    }

}