package com.hirix.jobalerts.application.service;

import com.hirix.jobalerts.domain.entity.JobPosting;
import com.hirix.jobalerts.domain.repository.JobPostingRepository;
import com.hirix.jobalerts.infrastructure.adzuna.dto.RawAdzunaJob;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class JobIngestionService {

    private static final Logger log = LoggerFactory.getLogger(JobIngestionService.class);

    private final JobFetchService jobFetchService;
    private final ExperienceClassifierService experienceClassifierService;
    private final JobFilterService jobFilterService;
    private final JobPostingService jobPostingService;
    private final JobPostingRepository jobPostingRepository;
    private final EmailService emailService;

    @Value("${alerts.to-email}")
    private String alertEmail;

    public int ingest(String query) {
        List<RawAdzunaJob> rawJobs = jobFetchService.fetchRawJobs(query);
        int newJobs = 0;

        for (RawAdzunaJob rawJob : rawJobs) {
            String applyUrl = normalizeApplyUrl(rawJob.redirectUrl());
            if (applyUrl == null) {
                continue;
            }

            boolean alreadyExists = jobPostingRepository.existsByApplyUrl(applyUrl);
            JobPosting mapped = mapToJobPosting(rawJob, applyUrl);
            mapped.setExperienceLevel(experienceClassifierService.classify(mapped.getTitle(), mapped.getDescription()));
            if (!jobFilterService.isAllowed(mapped)) {
                continue;
            }
            JobPosting saved = jobPostingService.saveIfNew(mapped);

            if (!alreadyExists) {
                newJobs++;
                log.info(
                        "New job detected:\nTitle: {}\nCompany: {}\nLocation: {}\nExperience Level: {}\nApply here: {}",
                        safeValue(saved.getTitle()),
                        safeValue(saved.getCompany()),
                        safeValue(saved.getLocation()),
                        saved.getExperienceLevel(),
                        safeValue(saved.getApplyUrl()));
            }
        }

        if (newJobs > 0) {
            emailService.sendEmail(
                    alertEmail,
                    "Nuevas vacantes encontradas ",
                    "Se encontraron " + newJobs + " nuevas vacantes.");
        }

        return newJobs;
    }

    private JobPosting mapToJobPosting(RawAdzunaJob rawJob, String normalizedApplyUrl) {
        return JobPosting.builder()
                .title(safeValue(rawJob.title()))
                .company(extractDisplayName(rawJob.company()))
                .location(extractDisplayName(rawJob.location()))
                .description(rawJob.description())
                .applyUrl(normalizedApplyUrl)
                .build();
    }

    private String extractDisplayName(Map<String, Object> nestedObject) {
        if (nestedObject == null || nestedObject.isEmpty()) {
            return "Unknown";
        }

        Object displayName = nestedObject.get("display_name");
        if (displayName != null) {
            return displayName.toString();
        }

        Object label = nestedObject.get("label");
        if (label != null) {
            return label.toString();
        }

        return nestedObject.values().iterator().next().toString();
    }

    private String normalizeApplyUrl(String applyUrl) {
        if (applyUrl == null) {
            return null;
        }

        String normalized = applyUrl.trim().toLowerCase();

        if (normalized.contains("/land/ad/")) {
            normalized = normalized.replace("/land/ad/", "/details/");
        }

        int index = normalized.indexOf("?");
        if (index != -1) {
            normalized = normalized.substring(0, index);
        }

        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized.isEmpty() ? null : normalized;
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "Unknown" : value;
    }
}
