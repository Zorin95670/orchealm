package com.orchealm.orchealmapi.model.error;

import lombok.Getter;

/**
 * Custom exception class to represent API-specific exceptions. This class encapsulates an {@link ErrorDTO} sent to the
 * client.
 *
 * <p>Logging depends on the HTTP status (see {@code GlobalExceptionHandler}): server errors (5xx) are logged as errors
 * with their stack trace, client errors (4xx) only at debug level.
 */
@Getter
public class ApiException extends RuntimeException {

    /**
     * The {@link ErrorDTO} associated with this exception, containing details about the error.
     */
    private final ErrorDTO error;

    /**
     * Constructs a new {@link ApiException} with the specified {@link ErrorDTO}.
     *
     * @param error The {@link ErrorDTO} containing details about the error.
     */
    public ApiException(final ErrorDTO error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Constructs a new {@link ApiException} with the specified {@link ErrorDTO} and underlying cause.
     *
     * <p>The cause is kept for logging only and is never sent to the client.
     *
     * @param error The {@link ErrorDTO} containing details about the error.
     * @param cause The exception that triggered this error.
     */
    public ApiException(final ErrorDTO error, final Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }
}
