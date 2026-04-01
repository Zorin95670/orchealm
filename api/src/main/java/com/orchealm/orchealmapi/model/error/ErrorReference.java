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
     * @return An {@link ErrorDTO} object containing the error response details with an HTTP status of 500 (Internal
     * Server Error).
     */
    public static ErrorDTO internalServerError(final String message) {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message(message)
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
     * Creates an {@link ErrorDTO} representing a forbidden access error.
     *
     * <p>Used when an authenticated user attempts to perform an action without sufficient permissions.
     * Missing or invalid authentication (401) is handled by Spring Security.
     *
     * @return an {@code ErrorDTO} with HTTP status 403 (Forbidden) and a default message
     */
    public static ErrorDTO forbidden() {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .message("You do not have permission to perform this action.")
            .build();
    }

    /**
     * Creates an {@link ErrorDTO} representing a concurrent modification of a resource.
     *
     * <p>Used when the {@code rowVersion} sent by the client is missing or does not match the current one, or when
     * the row was updated by another transaction in the meantime.
     *
     * @return an {@code ErrorDTO} with HTTP status 409 (Conflict) and a default message
     */
    public static ErrorDTO conflict() {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.CONFLICT.value())
            .message("The resource has been modified in the meantime or rowVersion is missing. "
                + "Reload it and try again.")
            .build();
    }

    /**
     * Creates an {@link ErrorDTO} representing an invalid parent project.
     *
     * <p>Used when the parent of a project does not exist, is not visible, belongs to another team, is not a master
     * project, or when the project itself has sub-projects.
     *
     * @return an {@code ErrorDTO} with HTTP status 400 (Bad Request) and a default message
     */
    public static ErrorDTO invalidProjectParent() {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message("The parent project must be a master project of the same team, "
                + "and a project with sub-projects cannot have a parent.")
            .build();
    }

    /**
     * Creates an {@link ErrorDTO} representing a resource that already exists.
     *
     * <p>Used when a write violates a unique constraint, e.g. an environment with the same short name in the team.
     *
     * @return an {@code ErrorDTO} with HTTP status 409 (Conflict) and a default message
     */
    public static ErrorDTO alreadyExists() {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.CONFLICT.value())
            .message("A resource with the same unique values already exists.")
            .build();
    }

    /**
     * Creates an {@link ErrorDTO} representing a reference to a resource that does not exist.
     *
     * <p>Used when a write violates a foreign key, e.g. an environment created for an unknown team.
     *
     * @return an {@code ErrorDTO} with HTTP status 400 (Bad Request) and a default message
     */
    public static ErrorDTO unknownReference() {
        return ErrorDTO.builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message("A referenced resource does not exist.")
            .build();
    }
}
