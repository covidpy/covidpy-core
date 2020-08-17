package py.gov.senatics.portal.annotation.formulario;


import java.lang.annotation.*;

/**
 * El campo anotado con esta anotación se mostrará si
 * otro campo conditionField cumple con el valor conditionValue.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(ConditionalFieldCollection.class)
public @interface ConditionalField {

    /**
     * Campo del cual depende este campo
     */
    String conditionField();

    /**
     * Valor que debe cumplir conditionField para mostrar este campo
     */
    String conditionValue();
}
