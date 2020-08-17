package py.gov.senatics.portal.validation.covid19;

import py.gov.senatics.portal.dto.covid19.admin.CambiarClaveDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SamePasswordValidator implements ConstraintValidator<SamePassword, CambiarClaveDto> {


    @Override
    public void initialize(SamePassword constraintAnnotation) {

    }

    @Override
    public boolean isValid(CambiarClaveDto value, ConstraintValidatorContext context) {
        if (value == null) {
            throw new IllegalArgumentException("@SamePassword solo se aplica a CambiarClaveDTO");
        }
        if (value.getPassword() != null && !value.getPassword().equals(value.getPassword2())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("password2")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
