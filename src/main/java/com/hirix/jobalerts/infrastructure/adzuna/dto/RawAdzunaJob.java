package com.hirix.jobalerts.infrastructure.adzuna.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawAdzunaJob(
        String id,
        String title,
        String description,
        Map<String, Object> location,
        @JsonProperty("redirect_url") String redirectUrl,
        @JsonProperty("created") String createdAt,
        Map<String, Object> company
) {
}
