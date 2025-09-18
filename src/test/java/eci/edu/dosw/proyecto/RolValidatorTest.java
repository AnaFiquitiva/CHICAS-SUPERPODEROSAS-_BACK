package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RolValidatorTest {

    @Test
    public void testEsRolValido() {
        assertTrue(RolValidator.esRolValido(Roles.ESTUDIANTE));
        assertTrue(RolValidator.esRolValido(Roles.DECANATURA));
        assertTrue(RolValidator.esRolValido(Roles.ADMINISTRADOR));
        assertFalse(RolValidator.esRolValido("INVALIDO"));
        assertFalse(RolValidator.esRolValido(null));
    }

    @Test
    public void testPuedeGestionarSolicitudes() {
        assertTrue(RolValidator.puedeGestionarSolicitudes(Roles.DECANATURA));
        assertTrue(RolValidator.puedeGestionarSolicitudes(Roles.ADMINISTRADOR));
        assertFalse(RolValidator.puedeGestionarSolicitudes(Roles.ESTUDIANTE));
        assertFalse(RolValidator.puedeGestionarSolicitudes("INVALIDO"));
    }

    @Test
    public void testPuedeCrearSolicitudes() {
        assertTrue(RolValidator.puedeCrearSolicitudes(Roles.ESTUDIANTE));
        assertFalse(RolValidator.puedeCrearSolicitudes(Roles.DECANATURA));
        assertFalse(RolValidator.puedeCrearSolicitudes(Roles.ADMINISTRADOR));
        assertFalse(RolValidator.puedeCrearSolicitudes("INVALIDO"));
    }
}