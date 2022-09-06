package br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Slug;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SlugValidator implements ConstraintValidator<Slug, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return Pattern.compile("^[a-z\\d]+(?:-[a-z\\d]+)*$").matcher(value).matches();
        }
        return true;
    }
}
