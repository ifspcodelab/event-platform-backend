package br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value.isBlank()) {
            return false;
        }
        return value.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\x20-\\x2F\\x3A-\\x40\\x5B-\\x60\\x7B-\\x7EáàâãéèêíóôõúçñÁÀÂÃÉÈÍÓÔÕÚÇ])[\\x20-\\x7FáàâãéèêíóôõúçñÁÀÂÃÉÈÍÓÔÕÚÇ]{8,64}$");
    }
}