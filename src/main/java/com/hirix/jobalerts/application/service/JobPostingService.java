package com.hirix.jobalerts.application.service;

import com.hirix.jobalerts.domain.entity.JobPosting;
import com.hirix.jobalerts.domain.repository.JobPostingRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    @Transactional
    public JobPosting saveIfNew(JobPosting job) {
        validate(job);
        String normalizedApplyUrl = normalizeApplyUrl(job.getApplyUrl());
        job.setApplyUrl(normalizedApplyUrl);

        return jobPostingRepository.findByApplyUrl(normalizedApplyUrl)
                .orElseGet(() -> saveNewOrFallbackLookup(job, normalizedApplyUrl));
    }

    private JobPosting saveNewOrFallbackLookup(JobPosting job, String normalizedApplyUrl) {
        try {
            return jobPostingRepository.save(job);
        } catch (DataIntegrityViolationException ex) {
            return jobPostingRepository.findByApplyUrl(normalizedApplyUrl)
                    .orElseThrow(() -> ex);
        }
    }

    private void validate(JobPosting job) {
        if (job == null) {
            throw new ValidationException("Job posting cannot be null.");
        }
        if (isBlank(job.getTitle())) {
            throw new ValidationException("Title is required.");
        }
        if (isBlank(job.getLocation())) {
            throw new ValidationException("Location is required.");
        }
        if (isBlank(job.getApplyUrl())) {
            throw new ValidationException("Apply URL is required.");
        }
    }

    private String normalizeApplyUrl(String applyUrl) {
        return applyUrl == null ? null : applyUrl.trim().toLowerCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
