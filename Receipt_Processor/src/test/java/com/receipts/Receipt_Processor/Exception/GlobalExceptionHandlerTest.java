package com.receipts.Receipt_Processor.Exception;

import com.receipts.Receipt_Processor.Exception.GlobalExceptionHandler;
import com.receipts.Receipt_Processor.Exception.InvalidDataException;
import com.receipts.Receipt_Processor.Exception.InvalidUUIDFormatException;
import com.receipts.Receipt_Processor.Exception.ReceiptNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleTypeMismatch_shouldReturnBadRequest() {
        // Arrange
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", UUID.class, "id", null, new IllegalArgumentException("Invalid UUID")
        );

        // Act
        ResponseEntity<?> response = exceptionHandler.handleTypeMismatch(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Invalid data type: id", body.get("error"));
        assertEquals("Expected type: UUID", body.get("message"));
    }

    @Test
    void handleInvalidUUIDFormatException_shouldReturnBadRequest() {
        // Arrange
        InvalidUUIDFormatException ex = new InvalidUUIDFormatException("Invalid UUID provided");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleInvalidUUIDFormatException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Invalid UUID format", body.get("error"));
        assertEquals("Invalid UUID provided", body.get("message"));
    }

    @Test
    void handleReceiptNotFoundException_shouldReturnNotFound() {
        // Arrange
        ReceiptNotFoundException ex = new ReceiptNotFoundException("Receipt with ID not found");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleReceiptNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Receipt not found", body.get("error"));
        assertEquals("Receipt with ID not found", body.get("message"));
    }

    @Test
    void handleInvalidDataException_shouldReturnBadRequest() {
        // Arrange
        InvalidDataException ex = new InvalidDataException("Invalid data provided");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleInvalidDataException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Invalid data", body.get("error"));
        assertEquals("Invalid data provided", body.get("message"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        // Arrange
        Exception ex = new Exception("Unexpected error occurred");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleGenericException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("Unexpected error occurred", body.get("message"));
    }
}

