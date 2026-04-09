package com.hirix.jobalerts.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.hirix.jobalerts.domain.entity.JobPosting;
import org.junit.jupiter.api.Test;

class JobFilterServiceTest {

    private final JobFilterService jobFilterService = new JobFilterService();

    @Test
    void shouldAllowGuadalajaraOnsiteJob() {
        JobPosting jobPosting = JobPosting.builder()
                .title("Backend Engineer")
                .location("Guadalajara, Jalisco, Mexico")
                .description("Onsite role")
                .applyUrl("https://example.com/1")
                .build();

        assertThat(jobFilterService.isAllowed(jobPosting)).isTrue();
    }

    @Test
    void shouldAllowRemoteJobWhenMexicoIsExplicitlyMentioned() {
        JobPosting jobPosting = JobPosting.builder()
                .title("Software Engineer - Remote")
                .location("Remote")
                .description("Open to candidates in Mexico")
                .applyUrl("https://example.com/2")
                .build();

        assertThat(jobFilterService.isAllowed(jobPosting)).isTrue();
    }

    @Test
    void shouldRejectRemoteJobWithoutMexicoMention() {
        JobPosting jobPosting = JobPosting.builder()
                .title("Software Engineer - Remote")
                .location("Remote")
                .description("Work from home worldwide")
                .applyUrl("https://example.com/3")
                .build();

        assertThat(jobFilterService.isAllowed(jobPosting)).isFalse();
    }

    @Test
    void shouldRejectJobOutsideGuadalajara() {
        JobPosting jobPosting = JobPosting.builder()
                .title("Accountant")
                .location("Monterrey, Mexico")
                .description("Onsite")
                .applyUrl("https://example.com/4")
                .build();

        assertThat(jobFilterService.isAllowed(jobPosting)).isFalse();
    }

    @Test
    void shouldRejectJobRestrictedToOtherCountries() {
        JobPosting jobPosting = JobPosting.builder()
                .title("Data Engineer - Remote")
                .location("Remote")
                .description("US only")
                .applyUrl("https://example.com/5")
                .build();

        assertThat(jobFilterService.isAllowed(jobPosting)).isFalse();
    }
}
