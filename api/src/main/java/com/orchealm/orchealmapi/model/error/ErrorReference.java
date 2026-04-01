package com.orchealm.orchealmapi.model.error;

import org.springframework.http.HttpStatus;

/**
 * The {@code ErrorReference} class provides utility methods for creating standardized error responses, such as internal
 * server errors, in a structured way.
 */
public final class ErrorReference {

    /**
     * Private constructor of the {@link ErrorReference} class.
     *
     * <p>This constructor is private to prevent instantiation of the {@link ErrorReference} class.
     * It ensures that this class cannot be instantiated and is intended solely for access to the static methods that
     * return instances of {@link ErrorDTO}.</p>
     */
    private ErrorReference() {
    }

    /**
     * Creates an {@link ErrorDTO} representing a 404 Not Found error.
     *
     * @return an {@code ErrorDTO} with HTTP status 404 and a default message
     */
    public static ErrorDTO notFound() {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .message("The requested resource was not found.")
            .build();
    }

    /**
     * Creates an {@link ErrorDTO} object representing an internal server error response.
     *
     * @param message A description of the error that occurred. This message will be included in the error response.
     * @param cause   The {@link Throwable} that caused the error, used to provide additional context or stack trace
     *                information.
     * @return An {@link ErrorDTO} object containing the error response details with an HTTP status of 500 (Internal
     * Server Error).
     */
    public static ErrorDTO internalServerError(final String message, final Throwable cause) {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message(message)
            .cause(cause)
            .build();
    }

    /**
     * Creates an {@link ErrorDTO} representing an entity-not-found error.
     *
     * <p>This method is typically used when a requested resource cannot be located based on a specific field and value
     * (e.g., an ID or unique attribute).
     *
     * @param field the name of the field used in the lookup (e.g., "id", "email")
     * @param value the value of the field that was not found
     * @return an {@code ErrorDTO} with a 500 Internal Server Error status and a descriptive message
     */
    public static ErrorDTO entityNotFound(final String field, final String value) {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .message(String.format("No entity found for field '%s' with value '%s'.", field, value))
            .build();
    }

    /**
     * Creates an {@link ErrorDTO} representing an unauthorized access error.
     *
     * <p>Typically used when a user attempts to perform an action without sufficient permissions.
     *
     * @return an {@code ErrorDTO} with HTTP status 401 (Unauthorized) and a default message
     */
    public static ErrorDTO unauthorized() {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .message("You do not have permission to perform this action.")
            .build();
    }

    public static ErrorDTO invalidJwtClaim(String name) {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .message(String.format("The JWT claim '%s' is invalid.", name))
            .build();
    }
}
