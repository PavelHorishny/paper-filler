package app.paper_filler.web.validation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PeselValidator.class)
@Target({ElementType.FIELD,Element.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pesel {
    String message() default "Wrong Pesel";
    Class<?>[] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
