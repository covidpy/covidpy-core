package py.gov.senatics.portal.annotation.formulario.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConditionalNotNull {

    String message() default "Es requerido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
