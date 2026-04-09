package com.hirix.jobalerts.infrastructure.adzuna.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adzuna")
public record AdzunaProperties(
        String baseUrl,
        String appId,
        String appKey,
        String country,
        int defaultResultsPerPage
) {
}
