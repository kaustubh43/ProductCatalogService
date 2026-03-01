package org.ecommerce.productcatalogservice.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleIllegalArgumentException returns 400 with error message")
    void handleIllegalArgumentException_returns400() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ResponseEntity<String> response = handler.handleIllegalArgumentException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid input");
        assertThat(response.getBody()).contains("error");
    }

    @Test
    @DisplayName("handleRuntimeException returns 500 with error message")
    void handleRuntimeException_returns500() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        ResponseEntity<String> response = handler.handleRuntimeException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Something went wrong");
        assertThat(response.getBody()).contains("error");
    }

    @Test
    @DisplayName("handleNullPointerException returns 500 with error message")
    void handleNullPointerException_returns500() {
        NullPointerException ex = new NullPointerException("Null value encountered");

        ResponseEntity<String> response = handler.handleNullPointerException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Null value encountered");
        assertThat(response.getBody()).contains("error");
    }

    @Test
    @DisplayName("handleGlobalException returns 500 with error message")
    void handleGlobalException_returns500() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<String> response = handler.handleGlobalException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Unexpected error");
        assertThat(response.getBody()).contains("error");
    }

    @Test
    @DisplayName("response body is valid JSON-like structure")
    void responseBody_containsJsonStructure() {
        RuntimeException ex = new RuntimeException("test error");

        ResponseEntity<String> response = handler.handleRuntimeException(ex);

        assertThat(response.getBody()).startsWith("{");
        assertThat(response.getBody()).endsWith("}");
        assertThat(response.getBody()).contains("\"error\"");
    }
}

