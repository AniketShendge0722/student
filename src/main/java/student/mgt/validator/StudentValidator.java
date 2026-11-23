
package student.mgt.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import student.mgt.dto.StudentDTO;

import java.time.LocalDate;

@Component
public class StudentValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return StudentDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        StudentDTO student = (StudentDTO) target;

        // Roll number validation
        if (student.getRollNo() == null
                || student.getRollNo().isBlank()) {

            errors.rejectValue(
                    "rollNo",
                    "student.rollNo.required",
                    "Student roll number is required."
            );

        } else if (!student.getRollNo()
                .matches("^[A-Za-z0-9/-]{1,20}$")) {

            errors.rejectValue(
                    "rollNo",
                    "student.rollNo.invalid",
                    "Enter a valid student roll number."
            );
        }

        // Student name validation
        if (student.getName() == null
                || student.getName().isBlank()) {

            errors.rejectValue(
                    "name",
                    "student.name.required",
                    "Student name is required."
            );

        } else if (!student.getName()
                .matches("^[\\p{L} .'-]{2,100}$")) {

            errors.rejectValue(
                    "name",
                    "student.name.invalid",
                    "Enter a valid student name."
            );
        }

        // Date of birth validation
        if (student.getDob() != null
                && !student.getDob().isBefore(LocalDate.now())) {

            errors.rejectValue(
                    "dob",
                    "student.dob.invalid",
                    "Date of birth must be in the past."
            );
        }

        // Parent selection validation
        if (student.getParentId() == null) {

            errors.rejectValue(
                    "parentId",
                    "student.parent.required",
                    "Please select a parent."
            );
        }
    }
}
