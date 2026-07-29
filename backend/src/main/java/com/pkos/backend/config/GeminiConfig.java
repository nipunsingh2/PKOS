package com.pkos.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.genai.Client;

@Configuration
public class GeminiConfig {

    @Bean
    public Client geminiClient(GeminiProperties properties) {
        return Client.builder()
                .apiKey(properties.getApiKey())
                .build();
    }
}