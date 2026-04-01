package com.orchealm.orchealmapi.model.error;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * Custom exception class to represent API-specific exceptions. This class encapsulates an {@link ErrorDTO} and provides
 * additional context for logging.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    /**
     * The {@link ErrorDTO} associated with this exception, containing details about the error.
     */
    private ErrorDTO error;

    /**
     * Flag indicating whether this exception needs to be logged. Default value is {@code true}, meaning the exception
     * should be logged unless explicitly set otherwise.
     */
    @Builder.Default
    private boolean needToBeLogged = true;

    /**
     * Constructs a new {@link ApiException} with the specified {@link ErrorDTO}.
     *
     * <p>This constructor sets the {@code error} field to the provided {@link ErrorDTO}, and sets
     * the {@code needToBeLogged} flag to {@code true} by default.
     *
     * @param error The {@link ErrorDTO} containing details about the error. This parameter is required and cannot be
     *              {@code null}.
     */
    public ApiException(final ErrorDTO error) {
        this.error = error;
        this.needToBeLogged = true;
    }
}

