package com.orchealm.orchealmapi.controller.handler;

import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void rowLevelSecurityViolationAnswersForbidden() {
        var exception = new JpaSystemException(new RuntimeException("could not execute statement",
            new SQLException("new row violates row-level security policy for table \"environments\"", "42501")));

        assertEquals(403, handler.handleDataAccessException(exception).getStatusCode().value());
    }

    @Test
    void uniqueViolationAnswersConflict(final CapturedOutput output) {
        var exception = new DataIntegrityViolationException("could not execute statement",
            new RuntimeException(new SQLException("duplicate key value violates unique constraint", "23505")));

        var response = handler.handleDataAccessException(exception);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("A resource with the same unique values already exists.", response.getBody().getMessage());
        assertFalse(output.getAll().contains("ERROR"));
    }

    @Test
    void foreignKeyViolationAnswersBadRequest() {
        var exception = new DataIntegrityViolationException("could not execute statement",
            new RuntimeException(new SQLException("violates foreign key constraint \"environments_team_key_fkey\"", "23503")));

        assertEquals(400, handler.handleDataAccessException(exception).getStatusCode().value());
    }

    @Test
    void otherDatabaseErrorAnswersInternalServerError() {
        var exception = new JpaSystemException(new RuntimeException("could not execute statement",
            new SQLException("value too long for type character varying(10)", "22001")));

        assertEquals(500, handler.handleDataAccessException(exception).getStatusCode().value());
    }

    @Test
    void clientErrorIsNotLoggedAsError(final CapturedOutput output) {
        var response = handler.handleApiException(new ApiException(ErrorReference.entityNotFound("id", "42")));

        assertEquals(404, response.getStatusCode().value());
        assertFalse(output.getAll().contains("ERROR"));
    }

    @Test
    void serverErrorIsLoggedAsErrorWithStackTrace(final CapturedOutput output) {
        var response = handler.handleApiException(
            new ApiException(ErrorReference.internalServerError("Error converting TstzRange"), new IllegalStateException("boom")));

        assertEquals(500, response.getStatusCode().value());
        assertTrue(output.getAll().contains("ERROR"));
        assertTrue(output.getAll().contains("Caused by: java.lang.IllegalStateException: boom"));
    }
}
