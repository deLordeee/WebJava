package org.example.cosmocats.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CosmicWordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CosmicWordCheck {
    String message() default "Field must contain specific cosmic-related terms like 'star', 'galaxy', 'comet', 'space', 'cosmic', 'intergalactic', 'orbit'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}