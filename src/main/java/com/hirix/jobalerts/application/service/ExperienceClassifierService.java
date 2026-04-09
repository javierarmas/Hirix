package com.hirix.jobalerts.application.service;

import com.hirix.jobalerts.domain.entity.ExperienceLevel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class ExperienceClassifierService {

    private static final Pattern RANGE_YEARS_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)\\s*years?");
    private static final Pattern PLUS_YEARS_PATTERN = Pattern.compile("(\\d+)\\s*\\+\\s*years?");
    private static final Pattern SINGLE_YEARS_PATTERN = Pattern.compile("(\\d+)\\s*years?");

    public ExperienceLevel classifyFromDescription(String description) {
        if (description == null || description.isBlank()) {
            return ExperienceLevel.UNKNOWN;
        }

        String normalized = description.toLowerCase();

        Matcher rangeMatcher = RANGE_YEARS_PATTERN.matcher(normalized);
        if (rangeMatcher.find()) {
            int minYears = Integer.parseInt(rangeMatcher.group(1));
            int maxYears = Integer.parseInt(rangeMatcher.group(2));
            return classifyRange(minYears, maxYears);
        }

        Matcher plusMatcher = PLUS_YEARS_PATTERN.matcher(normalized);
        if (plusMatcher.find()) {
            int minYears = Integer.parseInt(plusMatcher.group(1));
            return classifyMinOnly(minYears);
        }

        Matcher singleMatcher = SINGLE_YEARS_PATTERN.matcher(normalized);
        if (singleMatcher.find()) {
            int years = Integer.parseInt(singleMatcher.group(1));
            return classifyExactYears(years);
        }

        return ExperienceLevel.UNKNOWN;
    }

    private ExperienceLevel classifyRange(int minYears, int maxYears) {
        if (minYears >= 5) {
            return ExperienceLevel.SENIOR;
        }
        if (maxYears <= 2) {
            return ExperienceLevel.JUNIOR;
        }
        return ExperienceLevel.MID;
    }

    private ExperienceLevel classifyMinOnly(int minYears) {
        if (minYears >= 5) {
            return ExperienceLevel.SENIOR;
        }
        if (minYears >= 2) {
            return ExperienceLevel.MID;
        }
        return ExperienceLevel.JUNIOR;
    }

    private ExperienceLevel classifyExactYears(int years) {
        if (years >= 5) {
            return ExperienceLevel.SENIOR;
        }
        if (years <= 2) {
            return ExperienceLevel.JUNIOR;
        }
        return ExperienceLevel.MID;
    }
}
