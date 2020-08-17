package py.gov.senatics.portal.annotation.formulario;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Colección de Campos condicionales.
 * Se ejecuta en forma AND
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConditionalFieldCollection {
    ConditionalField[] value();
}
