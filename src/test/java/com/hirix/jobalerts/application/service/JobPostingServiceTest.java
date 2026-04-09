package com.hirix.jobalerts.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hirix.jobalerts.domain.entity.ExperienceLevel;
import com.hirix.jobalerts.domain.entity.JobPosting;
import com.hirix.jobalerts.domain.repository.JobPostingRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JobPostingServiceTest {

    @Autowired
    private JobPostingService service;

    @Autowired
    private JobPostingRepository repository;

    @Test
    void shouldSaveWhenUrlIsNew() {
        JobPosting saved = service.saveIfNew(buildJob("https://example.com/jobs/new-one"));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void shouldReturnExistingWhenUrlAlreadyExists() {
        JobPosting first = service.saveIfNew(buildJob("https://example.com/jobs/already-here"));
        JobPosting second = service.saveIfNew(buildJob("https://example.com/jobs/already-here"));

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void shouldNormalizeApplyUrlBeforeDeduplication() {
        JobPosting first = service.saveIfNew(buildJob("  HTTPS://EXAMPLE.COM/JOBS/NORMALIZE-ME  "));
        JobPosting second = service.saveIfNew(buildJob("https://example.com/jobs/normalize-me"));

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void shouldFailValidationForMissingRequiredFields() {
        JobPosting invalid = JobPosting.builder()
                .company("Acme")
                .description("2 years")
                .experienceLevel(ExperienceLevel.UNKNOWN)
                .build();

        assertThatThrownBy(() -> service.saveIfNew(invalid))
                .isInstanceOf(ValidationException.class);
    }

    private JobPosting buildJob(String applyUrl) {
        return JobPosting.builder()
                .title("Backend Engineer")
                .company("Acme")
                .location("Guadalajara, Mexico")
                .description("3 years")
                .applyUrl(applyUrl)
                .experienceLevel(ExperienceLevel.UNKNOWN)
                .build();
    }
}
