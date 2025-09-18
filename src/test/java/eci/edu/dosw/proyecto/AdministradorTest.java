package eci.edu.dosw.proyecto;
import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdministradorTest {

    private Administrador administrador;

    @BeforeEach
    public void setUp() {
        // Preparamos los datos de prueba para el administrador
        administrador = new Administrador("1", "12345", "Carlos Pérez", "carlos@uni.edu", "Facultad de Ingeniería");
    }

    @Test
    public void testActualizar() {
        // Verificamos que el método 'actualizar' imprime correctamente el mensaje esperado
        // La salida se puede capturar con un sistema de salida como ByteArrayOutputStream
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        administrador.actualizar();

        String expectedOutput = "Administrador Carlos Pérez ha sido notificado de cambios";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    public void testToString() {
        // Verificamos que el método toString() devuelva la cadena esperada
        String expectedString = "Administrador{id='1', codigo='12345', nombre='Carlos Pérez', email='carlos@uni.edu', facultad='Facultad de Ingeniería'}";
        assertEquals(expectedString, administrador.toString());
    }
}

