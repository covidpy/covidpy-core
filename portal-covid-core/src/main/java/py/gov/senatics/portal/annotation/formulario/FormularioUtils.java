package py.gov.senatics.portal.annotation.formulario;

import static org.apache.commons.beanutils.BeanUtils.getProperty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class FormularioUtils {

    /**
     * Controla si un campo anotado con ConditionalField cumple con las condiciones
     * dentro de un objeto
     * @param f campo anotado con ConditionalField
     * @param dto Objeto en el cuál debe cumplir la condición
     * @return Si cumple con las condiciones
     */
    public static boolean meetsCondition(Field f, Object dto) {
        return Arrays.stream(f.getAnnotationsByType(ConditionalField.class))
                .allMatch(
                        condition -> {
                            Object valorDto = null;
                            try {
                                valorDto = getProperty(dto, condition.conditionField());
                            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                            return valorDto != null && valorDto.equals(condition.conditionValue());
                        }
                );

    }
}
