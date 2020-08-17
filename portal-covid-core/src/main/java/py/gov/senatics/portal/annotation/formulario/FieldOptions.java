package py.gov.senatics.portal.annotation.formulario;

import java.lang.annotation.*;

/**
 * Colección de opciones para campos seleccionables
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldOptions {
    FieldOption[] value();
}
