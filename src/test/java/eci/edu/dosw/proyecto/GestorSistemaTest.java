package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.servicio.GestorSistema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GestorSistemaTest {

    private GestorSistema gestorSistema;

    @BeforeEach
    void setUp() {
        gestorSistema = new GestorSistema(); // Inicializar la clase
    }

    @Test
    void testMetodoGestionarSistema() {
        // Configura las entradas necesarias
        Sistema sistema = new Sistema();

        // Llama al método que quieres probar
        boolean resultado = gestorSistema.gestionarSistema(sistema);

        // Verifica el resultado esperado
        assertTrue(resultado);
    }

    @Test
    void testMetodoGestionarConError() {
        // Simula un escenario con error
        Sistema sistemaConError = null;

        // Verifica que la excepción se lance
        assertThrows(NullPointerException.class, () -> gestorSistema.gestionarSistema(sistemaConError));
    }
}
