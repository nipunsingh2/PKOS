package com.pkos.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "llm")
public class LLMProviderProperties {

    /**
     * Supported values:
     * gemini
     * openrouter
     */
    private String provider;

}