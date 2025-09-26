package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModelTests {

    @Test
    void testGetDescripcion() {
        assertEquals("Pendiente", EstadoSolicitud.PENDIENTE.getDescripcion());
        assertEquals("En revisión", EstadoSolicitud.EN_REVISION.getDescripcion());
        assertEquals("Aprobada", EstadoSolicitud.APROBADA.getDescripcion());
    }

    @Test
    void testEsEstadoFinal() {
        assertTrue(EstadoSolicitud.APROBADA.esEstadoFinal());
        assertTrue(EstadoSolicitud.RECHAZADA.esEstadoFinal());
        assertFalse(EstadoSolicitud.PENDIENTE.esEstadoFinal());
        assertFalse(EstadoSolicitud.EN_REVISION.esEstadoFinal());
    }

    @Test
    void testPuedeCambiarA() {
        assertTrue(EstadoSolicitud.PENDIENTE.puedeCambiarA(EstadoSolicitud.EN_REVISION));
        assertTrue(EstadoSolicitud.PENDIENTE.puedeCambiarA(EstadoSolicitud.RECHAZADA));
        assertFalse(EstadoSolicitud.PENDIENTE.puedeCambiarA(EstadoSolicitud.APROBADA));

        assertTrue(EstadoSolicitud.EN_REVISION.puedeCambiarA(EstadoSolicitud.APROBADA));
        assertTrue(EstadoSolicitud.EN_REVISION.puedeCambiarA(EstadoSolicitud.RECHAZADA));
        assertFalse(EstadoSolicitud.EN_REVISION.puedeCambiarA(EstadoSolicitud.PENDIENTE));

        assertFalse(EstadoSolicitud.APROBADA.puedeCambiarA(EstadoSolicitud.RECHAZADA));
    }

    @Test
    void testGetNombre() {
        assertEquals("Lunes", DiaSemana.LUNES.getNombre());
        assertEquals("Miércoles", DiaSemana.MIERCOLES.getNombre());
    }

    @Test
    void testGetNumeroDia() {
        assertEquals(1, DiaSemana.LUNES.getNumeroDia());
        assertEquals(6, DiaSemana.SABADO.getNumeroDia());
    }

    @Test
    void testEsDiaHabil() {
        assertTrue(DiaSemana.LUNES.esDiaHabil());
        assertTrue(DiaSemana.VIERNES.esDiaHabil());
        assertFalse(DiaSemana.SABADO.esDiaHabil());
    }

    @Test
    void testFromNumero() {
        assertEquals(DiaSemana.LUNES, DiaSemana.fromNumero(1));
        assertEquals(DiaSemana.SABADO, DiaSemana.fromNumero(6));
        assertThrows(IllegalArgumentException.class, () -> DiaSemana.fromNumero(7));
    }
    @Test
    void testTieneAltaPrioridad() {
        assertTrue(SemaforoAcademico.ROJO.tieneAltaPrioridad());
        assertFalse(SemaforoAcademico.VERDE.tieneAltaPrioridad());
        assertFalse(SemaforoAcademico.AZUL.tieneAltaPrioridad());
    }

    @Test
    void testFromPromedio() {
        assertEquals(SemaforoAcademico.VERDE, SemaforoAcademico.fromPromedio(4.0));
        assertEquals(SemaforoAcademico.VERDE, SemaforoAcademico.fromPromedio(3.5));
        assertEquals(SemaforoAcademico.AZUL, SemaforoAcademico.fromPromedio(3.0));
        assertEquals(SemaforoAcademico.AZUL, SemaforoAcademico.fromPromedio(2.5));
        assertEquals(SemaforoAcademico.ROJO, SemaforoAcademico.fromPromedio(2.0));
    }
    @Test
    void testPuedeGestionarSolicitudes() {
        assertTrue(RolUsuario.DECANO.puedeGestionarSolicitudes());
        assertTrue(RolUsuario.ADMINISTRADOR.puedeGestionarSolicitudes());
        assertFalse(RolUsuario.ESTUDIANTE.puedeGestionarSolicitudes());
        assertFalse(RolUsuario.PROFESOR.puedeGestionarSolicitudes());
    }

    @Test
    void testPuedeConfigurarSistema() {
        assertTrue(RolUsuario.ADMINISTRADOR.puedeConfigurarSistema());
        assertFalse(RolUsuario.DECANO.puedeConfigurarSistema());
        assertFalse(RolUsuario.PROFESOR.puedeConfigurarSistema());
        assertFalse(RolUsuario.ESTUDIANTE.puedeConfigurarSistema());
    }

    @Test
    void testPuedeVerReportes() {
        assertTrue(RolUsuario.ADMINISTRADOR.puedeVerReportes());
        assertTrue(RolUsuario.DECANO.puedeVerReportes());
        assertTrue(RolUsuario.PROFESOR.puedeVerReportes());
        assertFalse(RolUsuario.ESTUDIANTE.puedeVerReportes());
    }
}






