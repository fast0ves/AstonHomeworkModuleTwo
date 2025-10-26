package org.example.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgumentException_ReturnsBadRequestWithMessage() {
        String errorMessage = "Invalid input data";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<String> response = exceptionHandler.handleIllegalArgumentException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleGenericException_ReturnsInternalServerError() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<String> response = exceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }

    @Test
    void handleNullPointerException_ReturnsInternalServerError() {
        NullPointerException exception = new NullPointerException("Null pointer");

        ResponseEntity<String> response = exceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }
}
