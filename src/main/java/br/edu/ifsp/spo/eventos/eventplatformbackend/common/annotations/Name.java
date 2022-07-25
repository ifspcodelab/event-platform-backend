package br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators.NameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = NameValidator.class)
@Documented
public @interface Name {
    String message() default "deve conter apenas letras e espaços";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
