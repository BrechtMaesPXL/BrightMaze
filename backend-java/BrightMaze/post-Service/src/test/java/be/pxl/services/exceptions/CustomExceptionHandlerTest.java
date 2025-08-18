package be.pxl.services.exceptions;

import be.pxl.services.exception.CustomExceptionHandler;
import be.pxl.services.exception.EventValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionHandlerTest {

    private CustomExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new CustomExceptionHandler();
    }

    @Test
    void testHandleEventValidationException() {
        String errorMessage = "Invalid event data";
        EventValidationException exception = new EventValidationException(errorMessage);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleEventValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    void testHandleNotFoundException() {
        String errorMessage = "Resource not found";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<String> response = exceptionHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void testHandleGeneralException() {
        String errorMessage = "Something went wrong";
        Exception exception = new Exception(errorMessage);

        ResponseEntity<String> response = exceptionHandler.handleGeneralException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void testHandleEventValidationExceptionResponseFormat() {
        EventValidationException exception = new EventValidationException("Test message");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleEventValidationException(exception);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody() instanceof Map);
    }
}