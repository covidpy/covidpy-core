package py.gov.senatics.portal.annotation.formulario;

import java.lang.annotation.*;

/**
 * Opción del campo para los campos de tipo seleccionable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(FieldOptions.class)
public @interface FieldOption {

    /**
     * Valor a guardarse en la base de datos
     */
    String id();

    /**
     * Etiqueta a mostrar
     */
    String descripcion();
}
