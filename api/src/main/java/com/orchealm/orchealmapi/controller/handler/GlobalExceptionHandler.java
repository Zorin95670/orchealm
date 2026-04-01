package com.orchealm.orchealmapi.controller.handler;

import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorDTO;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.ErrorResponse;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * GlobalExceptionHandler is a centralized error handling class for Spring Boot applications. It uses @ControllerAdvice
 * to handle exceptions globally for all controllers.
 *
 * <p>This class provides custom responses for different types of exceptions:
 * <ul>
 *     <li>{@link ApiException}: the status and message of its {@link ErrorDTO};</li>
 *     <li>standard Spring MVC errors (404, 405, 400...): the status computed by {@link ResponseEntityExceptionHandler};</li>
 *     <li>database errors: 403 for a row-level security violation, 409 for an optimistic locking failure;</li>
 *     <li>anything else: 500 Internal Server Error.</li>
 * </ul>
 * <p>
 * This class ensures that the API responds with a consistent error structure across the application.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * PostgreSQL SQLState raised when a row-level security policy rejects a write.
     */
    private static final String INSUFFICIENT_PRIVILEGE = "42501";

    /**
     * PostgreSQL SQLState raised when a unique constraint is violated (the resource already exists).
     */
    private static final String UNIQUE_VIOLATION = "23505";

    /**
     * PostgreSQL SQLState raised when a foreign key references a row that does not exist (e.g. an unknown team).
     */
    private static final String FOREIGN_KEY_VIOLATION = "23503";
    /**
     * Handles general exceptions that are not explicitly handled elsewhere.
     *
     * @param exception The exception thrown
     * @return A ResponseEntity containing an ErrorDTO with status code and message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleAllExceptions(final Exception exception) {
        ErrorDTO errorDTO = ErrorDTO.builder()
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
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

        if (HttpStatus.valueOf(errorDTO.getStatusCode()).is5xxServerError()) {
            log.error(errorDTO.getMessage(), exception);
        } else {
            log.debug("Client error {}: {}", errorDTO.getStatusCode(), errorDTO.getMessage());
        }

        return new ResponseEntity<>(errorDTO, HttpStatus.valueOf(errorDTO.getStatusCode()));
    }

    /**
     * Handles database errors:
     * <ul>
     *     <li>a write rejected by a row-level security policy (SQLState {@code 42501}) answers 403. Services check
     *     permissions before writing, so this is a safety net for writes they do not cover;</li>
     *     <li>a unique constraint violation (SQLState {@code 23505}) means the resource already exists and answers
     *     409;</li>
     *     <li>a foreign key violation (SQLState {@code 23503}) means a referenced resource does not exist and answers
     *     400;</li>
     *     <li>any other database error is handled as an internal error.</li>
     * </ul>
     *
     * @param exception the database exception
     * @return a {@link ResponseEntity} with status 400, 403, 409 or 500
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorDTO> handleDataAccessException(final DataAccessException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (!(cause instanceof SQLException sqlException)) {
                continue;
            }

            if (INSUFFICIENT_PRIVILEGE.equals(sqlException.getSQLState())) {
                log.warn("Write rejected by a row-level security policy: {}", sqlException.getMessage());

                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorReference.forbidden());
            }

            if (UNIQUE_VIOLATION.equals(sqlException.getSQLState())) {
                log.debug("Unique constraint violation: {}", sqlException.getMessage());

                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ErrorReference.alreadyExists());
            }

            if (FOREIGN_KEY_VIOLATION.equals(sqlException.getSQLState())) {
                log.debug("Foreign key violation: {}", sqlException.getMessage());

                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorReference.unknownReference());
            }
        }

        return handleAllExceptions(exception);
    }

    /**
     * Handles optimistic locking failures: the row was updated by another transaction between read and write.
     *
     * @param exception the optimistic locking exception
     * @return a {@link ResponseEntity} with status 409
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorDTO> handleOptimisticLockingFailure(final OptimisticLockingFailureException exception) {
        log.info("Optimistic locking failure: {}", exception.getMessage());

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorReference.conflict());
    }

    /**
     * Formats the standard Spring MVC errors handled by {@link ResponseEntityExceptionHandler} (unknown URL 404,
     * method not allowed 405, unreadable body or failed validation 400, ...) as an {@link ErrorDTO}.
     *
     * <p>Validation errors are listed in {@code details.errors}.
     *
     * @param exception  the Spring MVC exception
     * @param body       the default body computed by Spring, ignored
     * @param headers    the response headers (e.g. {@code Allow} for 405)
     * @param statusCode the HTTP status computed by Spring
     * @param request    the current request
     * @return a {@link ResponseEntity} with the status computed by Spring and an {@link ErrorDTO} body
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(final Exception exception,
                                                             final Object body,
                                                             final HttpHeaders headers,
                                                             final HttpStatusCode statusCode,
                                                             final WebRequest request) {
        Map<String, Object> details = null;

        if (exception instanceof ErrorResponse errorResponse
            && statusCode.value() == HttpStatus.BAD_REQUEST.value()
            && errorResponse.getDetailMessageArguments() != null
            && errorResponse.getDetailMessageArguments().length > 0) {
            details = Map.of("errors", List.of(errorResponse.getDetailMessageArguments()));
        }

        HttpStatus status = HttpStatus.resolve(statusCode.value());

        log.debug("Client error {}: {}", statusCode.value(), exception.getMessage());

        ErrorDTO errorDTO = ErrorDTO.builder()
            .statusCode(statusCode.value())
            .message(status != null ? status.getReasonPhrase() : exception.getClass().getSimpleName())
            .details(details)
            .build();

        return ResponseEntity
            .status(statusCode)
            .headers(headers)
            .body(errorDTO);
    }
}
