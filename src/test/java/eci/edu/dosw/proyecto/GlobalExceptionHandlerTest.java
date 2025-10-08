package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests - Happy & Unhappy Paths")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MethodParameter methodParameter;

    // ========== HAPPY PATH TESTS ==========

    @Test
    @DisplayName("Happy Path - handleBusinessException con mensaje simple")
    void testHandleBusinessExceptionSuccess() {
        String errorMessage = "Error de negocio de prueba";
        BusinessException exception = new BusinessException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Error de negocio", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Happy Path - handleBusinessException con mensaje largo")
    void testHandleBusinessExceptionWithLongMessage() {
        String longMessage = "Este es un mensaje de error de negocio muy largo que contiene múltiples detalles " +
                "sobre lo que salió mal en la operación y proporciona información adicional al usuario";
        BusinessException exception = new BusinessException(longMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(longMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Happy Path - handleBusinessException con causa")
    void testHandleBusinessExceptionWithCause() {
        RuntimeException cause = new RuntimeException("Causa raíz");
        BusinessException exception = new BusinessException("Error de negocio", cause);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error de negocio", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Happy Path - handleNotFoundException con recurso no encontrado")
    void testHandleNotFoundExceptionSuccess() {
        String errorMessage = "Estudiante con ID STU001 no encontrado";
        NotFoundException exception = new NotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Recurso no encontrado", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Happy Path - handleNotFoundException con mensaje genérico")
    void testHandleNotFoundExceptionGeneric() {
        NotFoundException exception = new NotFoundException("Recurso no encontrado");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso no encontrado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Happy Path - handleNotFoundException con causa")
    void testHandleNotFoundExceptionWithCause() {
        RuntimeException cause = new RuntimeException("Causa de no encontrado");
        NotFoundException exception = new NotFoundException("Entidad no encontrada", cause);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entidad no encontrada", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Happy Path - handleValidationException con error de validación")
    void testHandleValidationExceptionSuccess() {
        String errorMessage = "El campo nombre es requerido";
        ValidationException exception = new ValidationException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Error de validación", response.getBody().getError());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Happy Path - handleValidationException con múltiples validaciones")
    void testHandleValidationExceptionMultipleErrors() {
        String errorMessage = "Errores de validación: campo1, campo2, campo3";
        ValidationException exception = new ValidationException(errorMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("campo1"));
    }

    @Test
    @DisplayName("Happy Path - handleValidationException con causa")
    void testHandleValidationExceptionWithCause() {
        RuntimeException cause = new RuntimeException("Causa de validación");
        ValidationException exception = new ValidationException("Error de validación", cause);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error de validación", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Happy Path - handleValidationExceptions con un campo")
    void testHandleMethodArgumentNotValidExceptionSingleField() {
        FieldError fieldError = new FieldError("student", "name", "El nombre es requerido");

        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Error de validación", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("name"));
        assertTrue(response.getBody().getMessage().contains("El nombre es requerido"));
    }

    @Test
    @DisplayName("Happy Path - handleValidationExceptions con múltiples campos")
    void testHandleMethodArgumentNotValidExceptionMultipleFields() {
        FieldError fieldError1 = new FieldError("student", "name", "El nombre es requerido");
        FieldError fieldError2 = new FieldError("student", "email", "El email no es válido");
        FieldError fieldError3 = new FieldError("student", "age", "La edad debe ser mayor a 0");

        List<ObjectError> errors = Arrays.asList(fieldError1, fieldError2, fieldError3);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String message = response.getBody().getMessage();
        assertTrue(message.contains("name"));
        assertTrue(message.contains("email"));
        assertTrue(message.contains("age"));
    }

    @Test
    @DisplayName("Happy Path - handleGenericException con Exception general")
    void testHandleGenericExceptionSuccess() {
        Exception exception = new Exception("Error inesperado del sistema");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Error interno del servidor", response.getBody().getError());
        assertEquals("Ocurrió un error inesperado", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Happy Path - handleGenericException con NullPointerException")
    void testHandleGenericExceptionNullPointer() {
        Exception exception = new NullPointerException("Referencia nula");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Ocurrió un error inesperado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Happy Path - handleGenericException con RuntimeException")
    void testHandleGenericExceptionRuntime() {
        Exception exception = new RuntimeException("Error de runtime");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocurrió un error inesperado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Happy Path - handleGenericException con IllegalArgumentException")
    void testHandleGenericExceptionIllegalArgument() {
        Exception exception = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Happy Path - handleGenericException con IllegalStateException")
    void testHandleGenericExceptionIllegalState() {
        Exception exception = new IllegalStateException("Estado ilegal");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocurrió un error inesperado", response.getBody().getMessage());
    }

    // ========== UNHAPPY PATH TESTS ==========

    @Test
    @DisplayName("Unhappy Path - handleBusinessException con mensaje null")
    void testHandleBusinessExceptionNullMessage() {
        BusinessException exception = new BusinessException(null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    @Test
    @DisplayName("Unhappy Path - handleBusinessException con mensaje vacío")
    void testHandleBusinessExceptionEmptyMessage() {
        BusinessException exception = new BusinessException("");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Unhappy Path - handleNotFoundException con mensaje null")
    void testHandleNotFoundExceptionNullMessage() {
        NotFoundException exception = new NotFoundException(null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    @Test
    @DisplayName("Unhappy Path - handleNotFoundException con mensaje vacío")
    void testHandleNotFoundExceptionEmptyMessage() {
        NotFoundException exception = new NotFoundException("");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Unhappy Path - handleValidationException con mensaje null")
    void testHandleValidationExceptionNullMessage() {
        ValidationException exception = new ValidationException(null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }

    @Test
    @DisplayName("Unhappy Path - handleValidationException con mensaje vacío")
    void testHandleValidationExceptionEmptyMessage() {
        ValidationException exception = new ValidationException("");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Unhappy Path - handleValidationExceptions sin errores")
    void testHandleMethodArgumentNotValidExceptionNoErrors() {
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("{}") ||
                response.getBody().getMessage().isEmpty() ||
                response.getBody().getMessage().equals("{}"));
    }

    @Test
    @DisplayName("Unhappy Path - handleValidationExceptions con mensaje de error null")
    void testHandleMethodArgumentNotValidExceptionNullErrorMessage() {
        FieldError fieldError = new FieldError("student", "name", null);

        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("name"));
    }

    @Test
    @DisplayName("Unhappy Path - handleGenericException con mensaje null")
    void testHandleGenericExceptionNullMessage() {
        Exception exception = new Exception((String) null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocurrió un error inesperado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Unhappy Path - handleGenericException con causa anidada")
    void testHandleGenericExceptionWithNestedCause() {
        Exception rootCause = new Exception("Causa raíz");
        Exception intermediateCause = new Exception("Causa intermedia", rootCause);
        Exception exception = new Exception("Error principal", intermediateCause);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocurrió un error inesperado", response.getBody().getMessage());
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("Edge Case - handleBusinessException con caracteres especiales")
    void testHandleBusinessExceptionSpecialCharacters() {
        String specialMessage = "Error: <>&\"'áéíóúñÑ@#$%^&*()";
        BusinessException exception = new BusinessException(specialMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(specialMessage, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Edge Case - handleValidationExceptions con campo vacío")
    void testHandleMethodArgumentNotValidExceptionEmptyField() {
        FieldError fieldError = new FieldError("student", "", "Error en campo vacío");

        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    @DisplayName("Edge Case - timestamps son recientes")
    void testAllHandlersReturnRecentTimestamps() {
        LocalDateTime before = LocalDateTime.now();

        ResponseEntity<ErrorResponse> response1 = globalExceptionHandler.handleBusinessException(
                new BusinessException("test"));
        ResponseEntity<ErrorResponse> response2 = globalExceptionHandler.handleNotFoundException(
                new NotFoundException("test"));
        ResponseEntity<ErrorResponse> response3 = globalExceptionHandler.handleValidationException(
                new ValidationException("test"));
        ResponseEntity<ErrorResponse> response4 = globalExceptionHandler.handleGenericException(
                new Exception("test"));

        LocalDateTime after = LocalDateTime.now();

        assertTrue(response1.getBody().getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(response1.getBody().getTimestamp().isBefore(after.plusSeconds(1)));

        assertTrue(response2.getBody().getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(response2.getBody().getTimestamp().isBefore(after.plusSeconds(1)));

        assertTrue(response3.getBody().getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(response3.getBody().getTimestamp().isBefore(after.plusSeconds(1)));

        assertTrue(response4.getBody().getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(response4.getBody().getTimestamp().isBefore(after.plusSeconds(1)));
    }

    @Test
    @DisplayName("Edge Case - response bodies nunca son null")
    void testAllHandlersReturnNonNullBodies() {
        ResponseEntity<ErrorResponse> response1 = globalExceptionHandler.handleBusinessException(
                new BusinessException("test"));
        ResponseEntity<ErrorResponse> response2 = globalExceptionHandler.handleNotFoundException(
                new NotFoundException("test"));
        ResponseEntity<ErrorResponse> response3 = globalExceptionHandler.handleValidationException(
                new ValidationException("test"));
        ResponseEntity<ErrorResponse> response4 = globalExceptionHandler.handleGenericException(
                new Exception("test"));

        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());
        assertNotNull(response3.getBody());
        assertNotNull(response4.getBody());
    }

    @Test
    @DisplayName("Edge Case - códigos de estado correctos")
    void testAllHandlersReturnCorrectStatusCodes() {
        ResponseEntity<ErrorResponse> response1 = globalExceptionHandler.handleBusinessException(
                new BusinessException("test"));
        ResponseEntity<ErrorResponse> response2 = globalExceptionHandler.handleNotFoundException(
                new NotFoundException("test"));
        ResponseEntity<ErrorResponse> response3 = globalExceptionHandler.handleValidationException(
                new ValidationException("test"));
        ResponseEntity<ErrorResponse> response4 = globalExceptionHandler.handleGenericException(
                new Exception("test"));

        assertEquals(400, response1.getBody().getStatus());
        assertEquals(404, response2.getBody().getStatus());
        assertEquals(400, response3.getBody().getStatus());
        assertEquals(500, response4.getBody().getStatus());
    }
}