package app.paper_filler.form.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PeselValidator implements ConstraintValidator<Pesel, String> {
    @Override 
    public boolean isValid(String value, ConstraintValidatorContext context){
        return value == null || PeselUtils.findError(value) == null;
    }
}
