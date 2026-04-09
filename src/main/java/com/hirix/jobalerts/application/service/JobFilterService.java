package com.hirix.jobalerts.application.service;

import com.hirix.jobalerts.domain.entity.JobPosting;
import java.text.Normalizer;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JobFilterService {

    private static final List<String> REMOTE_KEYWORDS = List.of(
            "remote", "remoto", "remota", "home office", "work from home", "wfh"
    );
    private static final List<String> MEXICO_KEYWORDS = List.of(
            "mexico", "mexican", "mx"
    );
    private static final List<String> GUADALAJARA_KEYWORDS = List.of(
            "guadalajara", "gdl"
    );
    private static final List<String> JALISCO_KEYWORDS = List.of(
            "jalisco"
    );
    private static final List<String> OTHER_COUNTRY_RESTRICTIONS = List.of(
            "us only",
            "u.s. only",
            "usa only",
            "united states only",
            "europe only",
            "eu only",
            "uk only",
            "united kingdom only",
            "canada only",
            "australia only",
            "spain only",
            "argentina only",
            "colombia only",
            "india only",
            "brazil only"
    );

    public boolean isAllowed(JobPosting jobPosting) {
        String searchableText = normalize(
                valueOrEmpty(jobPosting.getTitle()) + " "
                        + valueOrEmpty(jobPosting.getLocation()) + " "
                        + valueOrEmpty(jobPosting.getDescription())
        );

        boolean hasRemote = containsAny(searchableText, REMOTE_KEYWORDS);
        boolean mentionsMexico = containsAny(searchableText, MEXICO_KEYWORDS);
        boolean mentionsGuadalajara = containsAny(searchableText, GUADALAJARA_KEYWORDS);
        boolean mentionsJalisco = containsAny(searchableText, JALISCO_KEYWORDS);
        boolean hasOtherCountryRestriction = containsAny(searchableText, OTHER_COUNTRY_RESTRICTIONS);

        if (hasOtherCountryRestriction) {
            return false;
        }

        // Remote jobs must explicitly mention Mexico.
        if (hasRemote) {
            return mentionsMexico;
        }

        // Non-remote jobs must be in Guadalajara, Mexico.
        return mentionsGuadalajara && (mentionsMexico || mentionsJalisco);
    }

    private boolean containsAny(String text, List<String> keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
