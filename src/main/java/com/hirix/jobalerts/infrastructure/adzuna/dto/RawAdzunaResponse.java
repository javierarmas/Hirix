package com.hirix.jobalerts.infrastructure.adzuna.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawAdzunaResponse(
        Integer count,
        List<RawAdzunaJob> results
) {
}
