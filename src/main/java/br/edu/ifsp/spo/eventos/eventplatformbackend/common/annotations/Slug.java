package br.edu.ifsp.spo.eventos.eventplatformbackend.common.annotations;

import br.edu.ifsp.spo.eventos.eventplatformbackend.common.validators.SlugValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = SlugValidator.class)
@Documented
public @interface Slug {
    String message() default "Deve conter apenas letras minísculas, sem acentuação, sem cedilha, sem caracteres especiais e sem espaços. As palavras devem ser separadas por hífen.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
