package student.mgt.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = BusNumberValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBusNumber {

    String message() default
            "Invalid bus registration number. Example: MH 10 CR 3451";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}