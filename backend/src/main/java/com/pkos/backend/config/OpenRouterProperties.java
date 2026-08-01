package com.pkos.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "openrouter")
public class OpenRouterProperties {

    /**
     * OpenRouter API key.
     */
    private String apiKey;

    /**
     * Base URL.
     *
     * Example:
     * https://openrouter.ai/api/v1
     */
    private String baseUrl;

    /**
     * Default chat model.
     */
    private String chatModel;

    /**
     * Optional site URL sent to OpenRouter.
     */
    private String siteUrl;

    /**
     * Optional application name.
     */
    private String appName;

}