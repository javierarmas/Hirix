package com.hirix.jobalerts.application.scheduler;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ingestion.scheduler")
public class JobIngestionSchedulerProperties {

    private long fixedDelayMs = 300000;
    private List<String> queries = new ArrayList<>();
}
