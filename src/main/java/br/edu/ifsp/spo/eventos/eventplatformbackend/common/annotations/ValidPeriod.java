package br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators.ValidPeriodValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidPeriodValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPeriod {
    String message() default "Start date must be less than or equal to end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
