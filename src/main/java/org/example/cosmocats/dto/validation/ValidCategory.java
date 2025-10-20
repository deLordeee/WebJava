package org.example.cosmocats.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CategoryValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategory {
    String message() default "Invalid category. Allowed values: ANTI_GRAVITY_TOYS, COSMIC_FOOD, SPACE_THINGIES";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}