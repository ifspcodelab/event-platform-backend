package br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.Period;
import br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations.ValidPeriod;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPeriodValidator implements ConstraintValidator<ValidPeriod, Period> {

    @Override
    public void initialize(ValidPeriod constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Period period, ConstraintValidatorContext context) {
        return period.getStartDate().isBefore(period.getEndDate()) || period.getStartDate().isEqual(period.getEndDate());
    }
}
