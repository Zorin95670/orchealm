package com.orchealm.orchealmapi.model.error;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Data Transfer Object representing error details to be used in API responses. Encapsulates an error message, the
 * underlying cause (if any), and the HTTP status code.
 */
@Data
@Builder
@AllArgsConstructor
@Schema(description = "Standard error response returned by the API in case of failure.")
public class ErrorDTO {

    /**
     * Additional contextual details related to the exception.
     */
    @Schema(description = "Additional contextual details related to the exception.")
    private final Map<String, Object> details;

    /**
     * A human-readable message describing the error.
     */
    @Schema(description = "Error message describing what went wrong.", example = "Internal Server Error")
    private String message;

    /**
     * The underlying cause of the error, if available. This is typically a {@link Throwable} that triggered the error.
     */
    @Schema(description = "Optional cause of the error for debugging purposes.")
    private Throwable cause;

    /**
     * The HTTP status code associated with the error (e.g., 404, 500).
     */
    @Schema(description = "HTTP status code corresponding to the error.", example = "500")
    private int statusCode;
}

