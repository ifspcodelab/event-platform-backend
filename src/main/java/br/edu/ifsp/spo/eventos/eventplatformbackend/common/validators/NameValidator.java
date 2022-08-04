package br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Name;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<Name, String> {
    @Override
    public void initialize(Name constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.isBlank()) {
            return false;
        }
        return value.matches("^[a-zA-ZáàâãéèêíóôõúçñÁÀÂÃÉÈÍÓÔÕÚÇ'\\-`\\s]+ [a-zA-ZáàâãéèêíóôõúçñÁÀÂÃÉÈÍÓÔÕÚÇ'\\-`\\s]+$");
    }
}
