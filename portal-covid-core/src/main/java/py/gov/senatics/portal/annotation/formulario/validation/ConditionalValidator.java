package py.gov.senatics.portal.annotation.formulario.validation;

import org.apache.commons.beanutils.BeanUtils;
import py.gov.senatics.portal.annotation.formulario.ConditionalField;
import py.gov.senatics.portal.annotation.formulario.ConditionalFieldCollection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static py.gov.senatics.portal.annotation.formulario.FormularioUtils.meetsCondition;

/**
 * Validator para procesar entidades anotadas con ConditionalNotNull. Los campos anotados
 * con Conditional
 */
public class ConditionalValidator implements ConstraintValidator<ConditionalValidated, Object> {


    @Override
    public void initialize(ConditionalValidated constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Stream<Field> annotatedFields = Arrays.stream(value.getClass().getDeclaredFields())
                .filter(
                        f -> f.isAnnotationPresent(ConditionalNotNull.class)
                        && (
                                f.isAnnotationPresent(ConditionalField.class)
                                        || f.isAnnotationPresent(ConditionalFieldCollection.class)
                                )
                );

        List<Field> invalidFields = annotatedFields.filter(f -> {
            String fieldValue;
            try {
                fieldValue = BeanUtils.getProperty(value, f.getName());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return meetsCondition(f, value) &&
                    (fieldValue == null || (f.getType().equals(String.class) && fieldValue.trim().isEmpty()));
        }).collect(Collectors.toList());


        for (Field f: invalidFields) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(f.getName())
                    .addConstraintViolation();
        }

        return invalidFields.size() == 0;
    }
}
