package py.gov.senatics.portal.annotation.formulario;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {
    /**
     * Tipo de campo, define el tipo de input a renderizar
     */
    FormFieldType fieldType();

    /**
     * Etiqueta del campo
     */
    String label();

    /**
     * Para campos opcionales como Select, Checklist, Radio
     * Puede ser:
     * 1. URL del cuál obtener las opciones
     * 2. Lista de objetos en formato JSON con las opciones
     * Ejemplo: [{"id": true, "descripcion": "Sí"}, {"id": false, "descripcion": "No"}]}
     *
     */
    String optionsSource() default "";

    /**
     * Texto de ayuda en los campos
     */
    String hintText() default "";

    /**
     * ícono en los campos
     */
    String icon() default "";

    /**
     * Si el campo es de sólo lectura
     */
    boolean readonly() default false;


    /**
     * Propiedad a utilizarse como etiqueta para las opciones
     * para los tipos de campos seleccionables
     */
    String optionsTextProp() default "descripcion";

    /**
     * Propiedad a utilizarse como valor para las opciones
     * para los tipos de campos seleccionables
     */
    String optionsIdProp() default "id";

    /**
     * Paso en el que aparecerá el campo, en los formularios
     * de tipo wizard
     */
    int formStep() default 0;

    /**
     * Los campos con igual fila, estarán en la misma fila
     */
    int fila() default 0;

    /**
     * Nombre de la propiedad de la entidad en la que se guardará este campo
     */
    String modelField() default "";

    /**
     * Indice de página en la que aparecerá
     */
    int page() default 0;
}
