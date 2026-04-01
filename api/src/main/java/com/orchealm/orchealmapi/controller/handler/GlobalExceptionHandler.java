package com.orchealm.orchealmapi.controller.handler;

import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GlobalExceptionHandler is a centralized error handling class for Spring Boot applications. It uses @ControllerAdvice
 * to handle exceptions globally for all controllers.
 *
 * <p>This class provides custom responses for different types of exceptions:
 * - Handles general exceptions with a 500 Internal Server Error. - Handles ApiException with specific error details
 * contained in ErrorDTO. - Handles ResourceNotFoundException with a 404 Not Found response.</p>
 * <p>
 * This class ensures that the API responds with a consistent error structure across the application.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Handles general exceptions that are not explicitly handled elsewhere.
     *
     * @param exception The exception thrown
     * @return A ResponseEntity containing an ErrorDTO with status code and message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleAllExceptions(final Exception exception) {
        ErrorDTO errorDTO = ErrorDTO.builder()
            .message(exception.getMessage())
            .cause(exception.getCause())
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build();

        log.error("Uncaught exception", exception);

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles specific exceptions (like a custom ApiException).
     *
     * @param exception The ApiException thrown
     * @return A ResponseEntity containing an ErrorDTO with status code and message
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorDTO> handleApiException(final ApiException exception) {
        ErrorDTO errorDTO = exception.getError();

        if (exception.isNeedToBeLogged()) {
            log.error(errorDTO.getMessage(), exception);
        }

        return new ResponseEntity<>(errorDTO, HttpStatus.valueOf(errorDTO.getStatusCode()));
    }

    /**
     * Handles {@link HandlerMethodValidationException} thrown when validation on method parameters
     * such as {@code @RequestParam}, {@code @PathVariable}, or {@code @RequestHeader} fails.
     *
     * <p>Builds a structured {@link ErrorDTO} containing all validation error messages.</p>
     *
     * @param ex the validation exception
     * @return a {@link ResponseEntity} with status 400 and validation error messages
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorDTO> handleHandlerMethodValidationException(final HandlerMethodValidationException ex) {
        Map<String, Object> details = new HashMap<>();
        List<Object> errors = List.of(ex.getDetailMessageArguments());

        details.put("errors", errors);

        ErrorDTO errorDTO = ErrorDTO.builder()
            .cause(ex)
            .message("Validation failure")
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .details(details)
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorDTO);
    }
}
