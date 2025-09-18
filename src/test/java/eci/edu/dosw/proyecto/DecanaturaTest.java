package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DecanaturaTest {
    private Decanatura decanatura;
    private Estudiante estudiante;
    private Materia materia;
    private Grupo grupoOrigen;
    private Grupo grupoDestino;
    private SolicitudCambio solicitud;

    @BeforeEach
    public void setUp() {
        decanatura = new Decanatura("dec-001", "DEC001", "Carlos Perez",
                "c.perez@uni.edu", Facultades.INGENIERIA);

        estudiante = new Estudiante("est-001", "1234567890", "Juan Perez",
                "juan@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);

        materia = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
        grupoOrigen = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);
        grupoDestino = new Grupo("G2", materia, "Prof. López", "Martes 10-12", 25);

        solicitud = new SolicitudCambio("sol-001", estudiante, materia, grupoOrigen,
                materia, grupoDestino, "Cambio de horario");
    }

    @Test
    public void testCreacionDecanatura() {
        assertNotNull(decanatura);
        assertEquals("DEC001", decanatura.getCodigo());
        assertEquals("Carlos Perez", decanatura.getNombre());
        assertEquals(Roles.DECANATURA, decanatura.getRol());
    }

    @Test
    public void testAgregarSolicitud() {
        decanatura.agregarSolicitud(solicitud);
        assertEquals(1, decanatura.getSolicitudesPendientes().size());
        assertEquals(solicitud, decanatura.getSolicitudesPendientes().get(0));
    }

    @Test
    public void testEvaluarSolicitud() {
        decanatura.agregarSolicitud(solicitud);

        // No debería lanzar excepción
        assertDoesNotThrow(() -> decanatura.evaluarSolicitud(solicitud));
    }

    @Test
    public void testAprobarSolicitudConCupo() {
        decanatura.agregarSolicitud(solicitud);

        // Asegurar que hay cupo
        assertTrue(solicitud.validarCupo());

        assertDoesNotThrow(() -> decanatura.aprobarSolicitud(solicitud));
        assertEquals(EstadosSolicitud.APROBADO, solicitud.getEstadoString());
    }

    @Test
    public void testRechazarSolicitud() {
        decanatura.agregarSolicitud(solicitud);

        assertDoesNotThrow(() -> decanatura.rechazarSolicitud(solicitud, "Sin cupo disponible"));
        assertEquals(EstadosSolicitud.RECHAZADO, solicitud.getEstadoString());
    }

    @Test
    public void testActualizarNotificacion() {
        assertDoesNotThrow(() -> decanatura.actualizar());
    }
}
