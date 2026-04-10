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

    public ExperienceLevel classify(String title, String description) {

        String titleText = title == null ? "" : title.toLowerCase();
        String descText = description == null ? "" : description.toLowerCase();

        if (containsSenior(titleText)) {
            return ExperienceLevel.SENIOR;
        }

        if (containsJunior(titleText)) {
            return ExperienceLevel.JUNIOR;
        }

        if (containsMid(titleText)) {
            return ExperienceLevel.MID;
        }

        if (descText.isBlank()) {
            return ExperienceLevel.UNKNOWN;
        }

        Matcher rangeMatcher = RANGE_YEARS_PATTERN.matcher(descText);
        if (rangeMatcher.find()) {
            int minYears = Integer.parseInt(rangeMatcher.group(1));
            int maxYears = Integer.parseInt(rangeMatcher.group(2));
            return classifyRange(minYears, maxYears);
        }

        Matcher plusMatcher = PLUS_YEARS_PATTERN.matcher(descText);
        if (plusMatcher.find()) {
            int minYears = Integer.parseInt(plusMatcher.group(1));
            return classifyMinOnly(minYears);
        }

        Matcher singleMatcher = SINGLE_YEARS_PATTERN.matcher(descText);
        if (singleMatcher.find()) {
            int years = Integer.parseInt(singleMatcher.group(1));
            return classifyExactYears(years);
        }

        return ExperienceLevel.UNKNOWN;
    }

    private boolean containsSenior(String text) {
        return text.contains("senior") || text.contains("sr");
    }

    private boolean containsJunior(String text) {
        return text.contains("junior") || text.contains("jr");
    }

    private boolean containsMid(String text) {
        return text.contains("mid") || text.contains("middle");
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
