package com.hirix.jobalerts.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.hirix.jobalerts.domain.entity.ExperienceLevel;
import org.junit.jupiter.api.Test;

class ExperienceClassifierServiceTest {

    private final ExperienceClassifierService classifier = new ExperienceClassifierService();

    @Test
    void shouldClassifyPlusPattern() {
        ExperienceLevel level = classifier.classify(null, "We need 2+ years of experience with Java.");
        assertThat(level).isEqualTo(ExperienceLevel.MID);
    }

    @Test
    void shouldClassifySingleYearsPattern() {
        ExperienceLevel level = classifier.classify(null, "Requires 3 years in backend development.");
        assertThat(level).isEqualTo(ExperienceLevel.MID);
    }

    @Test
    void shouldClassifyRangePattern() {
        ExperienceLevel level = classifier.classify(null, "Candidates should have 5-7 years of experience.");
        assertThat(level).isEqualTo(ExperienceLevel.SENIOR);
    }

    @Test
    void shouldClassifyJuniorRange() {
        ExperienceLevel level = classifier.classify(null, "0-2 years experience");
        assertThat(level).isEqualTo(ExperienceLevel.JUNIOR);
    }

    @Test
    void shouldReturnUnknownWhenNoPatternIsFound() {
        ExperienceLevel level = classifier.classify(null, "Strong communication skills required.");
        assertThat(level).isEqualTo(ExperienceLevel.UNKNOWN);
    }

    @Test
    void shouldClassifyFromTitle() {
        ExperienceLevel level = classifier.classify("Senior Java Engineer", null);
        assertThat(level).isEqualTo(ExperienceLevel.SENIOR);
    }
}
