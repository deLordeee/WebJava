package org.example.cosmocats.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class CosmicWordValidator implements ConstraintValidator<CosmicWordCheck, String> {

    private static final List<String> COSMIC_TERMS = Arrays.asList(
            "star", "galaxy", "comet", "space", "cosmic", "intergalactic",
            "orbit", "planet", "solar", "lunar", "astro",
            "meteor", "black hole", "quasar", "pulsar", "constellation",
            "moon", "sun", "universe", "cosmos"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        String lowerCaseValue = value.toLowerCase().trim();

        log.debug("--- COSMIC VALIDATION ---");
        log.debug("Input: {}", value);
        log.debug("Lowercase: {}", lowerCaseValue);

        boolean valid = COSMIC_TERMS.stream().anyMatch(term -> {
            boolean contains = lowerCaseValue.contains(term);
            if (contains) {
                log.debug("Found term: {}", term);
            }
            return contains;
        });

        log.debug("Validation result: {}", valid);
        log.debug("-----------------------------");

        return valid;
    }
}
