package com.orchealm.orchealmapi.model.error;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Data Transfer Object representing error details to be used in API responses. Encapsulates an error message and the
 * HTTP status code. The underlying cause is never exposed: it is carried by {@link ApiException} and only logged.
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
     * The HTTP status code associated with the error (e.g., 404, 500).
     */
    @Schema(description = "HTTP status code corresponding to the error.", example = "500")
    private int statusCode;
}

