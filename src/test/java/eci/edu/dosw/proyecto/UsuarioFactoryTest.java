package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioFactoryTest {

    @Test
    public void testCrearEstudiante() {
        Estudiante estudiante = UsuarioFactory.crearEstudiante(
                "est-001", "1234567890", "Juan Perez",
                "juan@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3
        );

        assertNotNull(estudiante);
        assertEquals(Roles.ESTUDIANTE, estudiante.getRol());
        assertEquals("1234567890", estudiante.getCodigo());
    }

    @Test
    public void testCrearDecanatura() {
        Decanatura decanatura = UsuarioFactory.crearDecanatura(
                "dec-001", "DEC001", "Carlos Perez",
                "c.perez@uni.edu", Facultades.INGENIERIA
        );

        assertNotNull(decanatura);
        assertEquals(Roles.DECANATURA, decanatura.getRol());
        assertEquals("DEC001", decanatura.getCodigo());
    }

    @Test
    public void testCrearAdministrador() {
        Administrador administrador = UsuarioFactory.crearAdministrador(
                "admin-001", "ADM001", "Ana Gomez",
                "a.gomez@uni.edu", Facultades.INGENIERIA
        );

        assertNotNull(administrador);
        assertEquals(Roles.ADMINISTRADOR, administrador.getRol());
        assertEquals("ADM001", administrador.getCodigo());
    }
}