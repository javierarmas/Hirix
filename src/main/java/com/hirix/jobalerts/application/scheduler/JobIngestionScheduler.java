package com.hirix.jobalerts.application.scheduler;

import com.hirix.jobalerts.application.service.JobIngestionService;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class JobIngestionScheduler {

    private static final Logger log = LoggerFactory.getLogger(JobIngestionScheduler.class);

    private final JobIngestionService jobIngestionService;
    private final JobIngestionSchedulerProperties schedulerProperties;

    @Scheduled(fixedDelayString = "${ingestion.scheduler.fixed-delay-ms:300000}")
    public void runIngestionCycle() {
        Instant start = Instant.now();
        int totalNewJobs = 0;
        int executedQueries = 0;

        log.info("Job ingestion execution started.");

        for (String query : schedulerProperties.getQueries()) {
            if (query == null || query.isBlank()) {
                continue;
            }

            String trimmedQuery = query.trim();
            try {
                int newJobs = jobIngestionService.ingest(trimmedQuery);
                totalNewJobs += newJobs;
                executedQueries++;
                log.info("Job ingestion query completed. query='{}', newJobs={}", trimmedQuery, newJobs);
            } catch (Exception ex) {
                log.error("Job ingestion query failed. query='{}'", trimmedQuery, ex);
            }
        }

        long durationMs = Duration.between(start, Instant.now()).toMillis();
        log.info(
                "Job ingestion execution finished. executedQueries={}, totalNewJobs={}, durationMs={}",
                executedQueries,
                totalNewJobs,
                durationMs
        );
    }
}
