
package student.mgt.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import student.mgt.util.BusNumberUtil;

public class BusNumberValidator
        implements ConstraintValidator<ValidBusNumber, String> {

    @Override
    public boolean isValid(
            String busNumber,
            ConstraintValidatorContext context) {

        // Required-field validation is handled by @NotBlank.
        if (busNumber == null || busNumber.isBlank()) {
            return true;
        }

        return BusNumberUtil.isValid(busNumber);
    }
}
