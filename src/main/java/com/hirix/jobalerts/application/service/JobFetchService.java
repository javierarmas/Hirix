package com.hirix.jobalerts.application.service;

import com.hirix.jobalerts.infrastructure.adzuna.config.AdzunaProperties;
import com.hirix.jobalerts.infrastructure.adzuna.dto.RawAdzunaJob;
import com.hirix.jobalerts.infrastructure.adzuna.dto.RawAdzunaResponse;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class JobFetchService {

    private final RestTemplate adzunaRestTemplate;
    private final AdzunaProperties adzunaProperties;

    public List<RawAdzunaJob> fetchRawJobs(String query) {
        return fetchRawJobs(query, 1, adzunaProperties.defaultResultsPerPage());
    }

    public List<RawAdzunaJob> fetchRawJobs(String query, int page, int resultsPerPage) {
        validateConfiguration();

        String url = UriComponentsBuilder
                .fromHttpUrl(adzunaProperties.baseUrl())
                .pathSegment("v1", "api", "jobs", adzunaProperties.country(), "search", String.valueOf(page))
                .queryParam("app_id", adzunaProperties.appId())
                .queryParam("app_key", adzunaProperties.appKey())
                .queryParam("what", query)
                .queryParam("results_per_page", resultsPerPage)
                .build()
                .toUriString();

        RawAdzunaResponse response = adzunaRestTemplate.getForObject(url, RawAdzunaResponse.class);
        if (response == null || response.results() == null) {
            return Collections.emptyList();
        }
        return response.results();
    }

    private void validateConfiguration() {
        requireText(adzunaProperties.baseUrl(), "adzuna.base-url");
        requireText(adzunaProperties.appId(), "adzuna.app-id");
        requireText(adzunaProperties.appKey(), "adzuna.app-key");
        requireText(adzunaProperties.country(), "adzuna.country");
    }

    private void requireText(String value, String propertyName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException("Missing required property: " + propertyName);
        }
    }
}
