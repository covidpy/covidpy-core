package py.gov.senatics.portal.annotation.formulario;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tamaño del input. Se usa la unidad de medida "ch".
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormInputLength {

    int value();
}
