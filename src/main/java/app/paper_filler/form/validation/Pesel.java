package app.paper_filler.form.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = PeselValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pesel {
    String message() default "Wrong Pesel";
    Class<?>[] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
