package student.mgt.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Object createErrorResponse(
            HttpServletRequest request,
            HttpStatus status,
            String message,
            Map<String, String> validationErrors) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message,
                request.getRequestURI(),
                validationErrors
        );

        if (request.getRequestURI().startsWith("/api/")) {

            return ResponseEntity
                    .status(status)
                    .body(error);
        }

        ModelAndView modelAndView = new ModelAndView("error");

        modelAndView.addObject("error", error);
        modelAndView.addObject("status", status.value());
        modelAndView.addObject("message", message);
        modelAndView.addObject("path", request.getRequestURI());

        return modelAndView;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return createErrorResponse(
                request,
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public Object handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        return createErrorResponse(
                request,
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(BusCapacityExceededException.class)
    public Object handleBusCapacity(
            BusCapacityExceededException ex,
            HttpServletRequest request) {

        return createErrorResponse(
                request,
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class
    })
    public Object handleValidationErrors(
            Exception ex,
            HttpServletRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        if (ex instanceof MethodArgumentNotValidException validationEx) {

            validationEx.getBindingResult()
                    .getFieldErrors()
                    .forEach(error ->
                            errors.putIfAbsent(
                                    error.getField(),
                                    error.getDefaultMessage()
                            )
                    );

        } else if (ex instanceof BindException validationEx) {

            validationEx.getBindingResult()
                    .getFieldErrors()
                    .forEach(error ->
                            errors.putIfAbsent(
                                    error.getField(),
                                    error.getDefaultMessage()
                            )
                    );
        }

        return createErrorResponse(
                request,
                HttpStatus.BAD_REQUEST,
                "Please correct the validation errors.",
                errors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getConstraintViolations().forEach(violation ->
                errors.put(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                )
        );

        return createErrorResponse(
                request,
                HttpStatus.BAD_REQUEST,
                "Request validation failed.",
                errors
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDatabaseIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        return createErrorResponse(
                request,
                HttpStatus.CONFLICT,
                "This record cannot be saved because it conflicts with existing database records or related data.",
                null
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return createErrorResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public Object handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        return createErrorResponse(
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                null
        );
    }
}