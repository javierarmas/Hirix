package com.hirix.jobalerts.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hirix.jobalerts.domain.entity.ExperienceLevel;
import com.hirix.jobalerts.domain.entity.JobPosting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class JobPostingRepositoryTest {

    @Autowired
    private JobPostingRepository repository;

    @Test
    void shouldPersistJobPostingWithValidFields() {
        JobPosting saved = repository.save(buildJob("https://example.com/jobs/1"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRejectDuplicateApplyUrl() {
        repository.saveAndFlush(buildJob("https://example.com/jobs/duplicate"));

        assertThatThrownBy(() -> repository.saveAndFlush(buildJob("https://example.com/jobs/duplicate")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldFindAndCheckByApplyUrl() {
        String url = "https://example.com/jobs/find-me";
        repository.saveAndFlush(buildJob(url));

        assertThat(repository.existsByApplyUrl(url)).isTrue();
        assertThat(repository.findByApplyUrl(url)).isPresent();
    }

    private JobPosting buildJob(String applyUrl) {
        return JobPosting.builder()
                .title("Software Engineer")
                .company("Acme")
                .location("Guadalajara, Mexico")
                .description("2+ years")
                .applyUrl(applyUrl)
                .experienceLevel(ExperienceLevel.UNKNOWN)
                .build();
    }
}
