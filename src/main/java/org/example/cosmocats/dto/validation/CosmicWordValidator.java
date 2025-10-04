package org.example.cosmocats.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

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

        // Debug output (remove in production)
        System.out.println("--- COSMIC VALIDATION ---");
        System.out.println("Input: " + value);
        System.out.println("Lowercase: " + lowerCaseValue);

        boolean valid = COSMIC_TERMS.stream().anyMatch(term -> {
            boolean contains = lowerCaseValue.contains(term);
            if (contains) {
                System.out.println("Found  term: " + term);
            }
            return contains;
        });

        System.out.println("Validation result: " + valid);
        System.out.println("-----------------------------");

        return valid;
    }
}