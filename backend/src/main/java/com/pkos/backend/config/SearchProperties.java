package com.pkos.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pkos.search")
public class SearchProperties {

    private double keywordWeight = 0.6;

    private double semanticWeight = 0.4;

    private int maxResults = 100;

    private int snippetLength = 200;

    private double semanticThreshold = 0.0;

}