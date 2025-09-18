package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EstadoSolicitudTest {
    private SolicitudCambio solicitud;
    private EstadoSolicitud pendiente;
    private EstadoSolicitud enRevision;
    private EstadoSolicitud aprobado;
    private EstadoSolicitud rechazado;

    @BeforeEach
    public void setUp() {
        Estudiante estudiante = new Estudiante("est-001", "1234567890", "Juan Perez",
                "juan@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);
        Materia materia = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
        Grupo grupo = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);

        solicitud = new SolicitudCambio("sol-001", estudiante, materia, grupo,
                materia, grupo, "Cambio");

        pendiente = new Pendiente(solicitud);
        enRevision = new EnRevision(solicitud);
        aprobado = new Aprobado(solicitud);
        rechazado = new Rechazado(solicitud);
    }

    @Test
    public void testPendientePuedeAprobar() {
        assertTrue(pendiente.puedeAprobar());
        assertDoesNotThrow(() -> pendiente.procesar());
    }

    @Test
    public void testEnRevisionPuedeAprobar() {
        assertTrue(enRevision.puedeAprobar());
        assertDoesNotThrow(() -> enRevision.procesar());
    }

    @Test
    public void testAprobadoNoPuedeAprobar() {
        assertFalse(aprobado.puedeAprobar());
        assertDoesNotThrow(() -> aprobado.procesar());
    }

    @Test
    public void testRechazadoNoPuedeAprobar() {
        assertFalse(rechazado.puedeAprobar());
        assertDoesNotThrow(() -> rechazado.procesar());
    }

    @Test
    public void testToStringEstados() {
        assertEquals("Pendiente", pendiente.toString());
        assertEquals("En Revisión", enRevision.toString());
        assertEquals("Aprobado", aprobado.toString());
        assertEquals("Rechazado", rechazado.toString());
    }
}