package eci.edu.dosw.proyecto;
import eci.edu.dosw.proyecto.exception.BusinessException;
import eci.edu.dosw.proyecto.exception.ErrorResponse;
import eci.edu.dosw.proyecto.exception.NotFoundException;
import eci.edu.dosw.proyecto.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exception Tests")
class ExceptionTests {

    @Test
    @DisplayName("BusinessException - Constructor con mensaje")
    void testBusinessExceptionWithMessage() {
        String errorMessage = "Error de negocio";
        BusinessException exception = new BusinessException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("BusinessException - Constructor con mensaje y causa")
    void testBusinessExceptionWithMessageAndCause() {
        String errorMessage = "Error de negocio";
        Throwable cause = new RuntimeException("Causa original");
        BusinessException exception = new BusinessException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("NotFoundException - Constructor con mensaje")
    void testNotFoundExceptionWithMessage() {
        String errorMessage = "Recurso no encontrado";
        NotFoundException exception = new NotFoundException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("NotFoundException - Constructor con mensaje y causa")
    void testNotFoundExceptionWithMessageAndCause() {
        String errorMessage = "Recurso no encontrado";
        Throwable cause = new RuntimeException("Causa original");
        NotFoundException exception = new NotFoundException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("ValidationException - Constructor con mensaje")
    void testValidationExceptionWithMessage() {
        String errorMessage = "Error de validación";
        ValidationException exception = new ValidationException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("ValidationException - Constructor con mensaje y causa")
    void testValidationExceptionWithMessageAndCause() {
        String errorMessage = "Error de validación";
        Throwable cause = new RuntimeException("Causa original");
        ValidationException exception = new ValidationException(errorMessage, cause);

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("ErrorResponse - Constructor y getters")
    void testErrorResponse() {
        java.time.LocalDateTime timestamp = java.time.LocalDateTime.now();
        int status = 400;
        String error = "Bad Request";
        String message = "Error de validación";

        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message);

        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
    }

    @Test
    @DisplayName("ErrorResponse - Setters")
    void testErrorResponseSetters() {
        ErrorResponse errorResponse = new ErrorResponse(
                java.time.LocalDateTime.now(),
                400,
                "Bad Request",
                "Original message"
        );

        java.time.LocalDateTime newTimestamp = java.time.LocalDateTime.now().plusHours(1);
        errorResponse.setTimestamp(newTimestamp);
        errorResponse.setStatus(500);
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage("New message");

        assertEquals(newTimestamp, errorResponse.getTimestamp());
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("New message", errorResponse.getMessage());
    }

    @Test
    @DisplayName("ErrorResponse - Equals y HashCode")
    void testErrorResponseEqualsAndHashCode() {
        java.time.LocalDateTime timestamp = java.time.LocalDateTime.now();
        ErrorResponse response1 = new ErrorResponse(timestamp, 400, "Error", "Message");
        ErrorResponse response2 = new ErrorResponse(timestamp, 400, "Error", "Message");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("ErrorResponse - ToString")
    void testErrorResponseToString() {
        ErrorResponse errorResponse = new ErrorResponse(
                java.time.LocalDateTime.now(),
                404,
                "Not Found",
                "Resource not found"
        );

        String toString = errorResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("404"));
        assertTrue(toString.contains("Not Found"));
    }
}