package eci.edu.dosw.proyecto.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    @DisplayName("Debería crear ErrorResponse con valores válidos y un timestamp actual")
    void deberiaCrearErrorResponseConValoresValidos() {
        // Arrange: Definimos los valores de entrada
        String expectedCode = "ERR_404";
        String expectedMessage = "Recurso no encontrado";

        // Capturamos el tiempo justo antes y después de la creación para validar el timestamp
        long beforeCreation = System.currentTimeMillis();

        // Act: Creamos la instancia del objeto
        ErrorResponse errorResponse = new ErrorResponse(expectedCode, expectedMessage);

        long afterCreation = System.currentTimeMillis();

        // Assert: Verificamos que los valores se hayan asignado correctamente
        assertEquals(expectedCode, errorResponse.getCode(), "El código de error no coincide");
        assertEquals(expectedMessage, errorResponse.getMessage(), "El mensaje de error no coincide");

        // Verificamos que el timestamp esté dentro del rango esperado
        assertTrue(errorResponse.getTimestamp() >= beforeCreation && errorResponse.getTimestamp() <= afterCreation,
                "El timestamp no se generó en el momento de la creación");
    }

    @Test
    @DisplayName("Debería crear ErrorResponse con valores nulos (caso límite)")
    void deberiaCrearErrorResponseConValoresNulos() {
        // Arrange: Usamos valores nulos
        String expectedCode = null;
        String expectedMessage = null;

        // Act
        ErrorResponse errorResponse = new ErrorResponse(expectedCode, expectedMessage);

        // Assert: Verificamos que los getters devuelven nulo
        assertNull(errorResponse.getCode(), "El código debería ser nulo");
        assertNull(errorResponse.getMessage(), "El mensaje debería ser nulo");
        // El timestamp siempre debe generarse, incluso si los otros valores son nulos
        assertTrue(errorResponse.getTimestamp() > 0, "El timestamp debería ser mayor a cero");
    }

    @Test
    @DisplayName("Debería permitir modificar el mensaje usando el setter de Lombok")
    void deberiaPermitirSetearYObtenerValores() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse("ERR_001", "Error inicial");
        String newMessage = "Mensaje modificado";

        // Act: Usamos el setter generado por Lombok
        errorResponse.setMessage(newMessage);

        // Assert
        assertEquals(newMessage, errorResponse.getMessage(), "El mensaje no fue actualizado correctamente");
        assertEquals("ERR_001", errorResponse.getCode(), "El código no debería haber cambiado");
    }
}