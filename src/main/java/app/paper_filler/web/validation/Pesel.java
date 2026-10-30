package app.paper_filler.web.validation;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PeselValidator.class)
@Target({ElementType.FIELD,Element.PARAMETR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pesel {
    String message() default "Wrong Pesel";
    Class<?>[] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
