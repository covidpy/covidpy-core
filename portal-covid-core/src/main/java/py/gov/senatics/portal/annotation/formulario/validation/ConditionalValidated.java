package py.gov.senatics.portal.annotation.formulario.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ConditionalValidator.class)
@Documented
public @interface ConditionalValidated {

    String message() default "Es requerido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
