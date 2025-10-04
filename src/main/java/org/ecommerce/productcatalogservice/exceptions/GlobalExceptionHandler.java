package org.ecommerce.productcatalogservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler handles exceptions thrown by controller methods and maps them to appropriate HTTP responses.
 * It provides centralized exception handling for IllegalArgumentException, RuntimeException, and NullPointerException.
 * More to be added if required
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException thrown by controller methods.
     * Returns a 400 Bad Request response with the exception message.
     *
     * @param exception the IllegalArgumentException thrown
     * @return ResponseEntity with the error message and HTTP 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(Exception exception){
        return new ResponseEntity<>("{\"error\": \"" + exception.getMessage() + "\"}", HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles RuntimeException thrown by controller methods (excluding NullPointerException).
     * Returns a 500 Internal Server Error response with the exception message.
     *
     * @param exception the RuntimeException thrown
     * @return ResponseEntity with the error message and HTTP 500 status
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles NullPointerException thrown by controller methods.
     * Returns a 500 Internal Server Error response with the exception message.
     *
     * @param exception the NullPointerException thrown
     * @return ResponseEntity with error the message and HTTP 500 status
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
