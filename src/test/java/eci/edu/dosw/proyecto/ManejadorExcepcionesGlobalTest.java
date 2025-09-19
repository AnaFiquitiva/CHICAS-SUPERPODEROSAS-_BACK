package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.excepcion.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

public class ManejadorExcepcionesGlobalTest {

    private ManejadorExcepcionesGlobal manejador = new ManejadorExcepcionesGlobal();

    @Test
    public void testManejarExcepcionRuntime() {
        RuntimeException ex = new RuntimeException("Error de prueba");

        ResponseEntity<Map<String, String>> response =
                manejador.manejarExcepcionRuntime(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error de negocio", response.getBody().get("error"));
        assertEquals("Error de prueba", response.getBody().get("mensaje"));
    }

    @Test
    public void testManejarValidacionException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "campo", "mensaje error");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response =
                manejador.manejarValidacionException(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().get("errores").contains("campo=mensaje error"));
    }

    @Test
    public void testManejarExcepcionGeneral() {
        Exception ex = new Exception("Error interno");

        ResponseEntity<Map<String, String>> response =
                manejador.manejarExcepcionGeneral(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error interno del servidor", response.getBody().get("error"));
    }
}