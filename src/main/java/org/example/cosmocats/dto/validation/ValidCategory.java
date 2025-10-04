package org.example.cosmocats.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Pattern(regexp = "ANTI_GRAVITY_TOYS|COSMIC_FOOD|SPACE_THINGIES",
        message = "Category must be : ANTI_GRAVITY_TOYS, COSMIC_FOOD, SPACE_THINGIES")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategory {
    String message() default "Invalid category";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}