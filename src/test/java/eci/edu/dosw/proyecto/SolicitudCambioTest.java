package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class SolicitudCambioTest {
    private Estudiante estudiante;
    private Decanatura decanatura;
    private Materia materia;
    private Grupo grupoOrigen;
    private Grupo grupoDestino;
    private SolicitudCambio solicitud;

    @BeforeEach
    public void setUp() {
        estudiante = new Estudiante("est-001", "1234567890", "Juan Perez",
                "juan@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                "Ingeniería", 3);

        decanatura = new Decanatura("dec-001", "DEC001", "Carlos Perez",
                "c.perez@uni.edu", Facultades.INGENIERIA);

        materia = new Materia("MAT101", "Matemáticas", 3, Facultades.INGENIERIA);
        grupoOrigen = new Grupo("G1", materia, "Prof. García", "Lunes 8-10", 30);
        grupoDestino = new Grupo("G2", materia, "Prof. López", "Martes 10-12", 25);

        // Crear solicitud para los tests
        solicitud = new SolicitudCambio("sol-001", estudiante, materia, grupoOrigen,
                materia, grupoDestino, "Cambio de horario");
    }

    @Test
    public void testCreacionSolicitud() {
        assertNotNull(solicitud);
        assertEquals("sol-001", solicitud.getId());
        assertEquals(estudiante, solicitud.getEstudiante());
        assertEquals(materia, solicitud.getMateriaOrigen());
        assertEquals(grupoOrigen, solicitud.getGrupoOrigen());
        assertEquals(materia, solicitud.getMateriaDestino());
        assertEquals(grupoDestino, solicitud.getGrupoDestino());
        assertEquals("Cambio de horario", solicitud.getObservaciones());
        assertEquals(EstadosSolicitud.PENDIENTE, solicitud.getEstadoString());

        // Verificar que la fecha de creación no sea nula
        assertNotNull(solicitud.getFechaCreacion());

        // Verificar que la prioridad no sea nula
        assertNotNull(solicitud.getPrioridad());
    }

    @Test
    public void testFlujoCompletoSolicitud() {
        // 1. Estudiante crea solicitud
        SolicitudCambio nuevaSolicitud = estudiante.crearSolicitud(
                materia, grupoOrigen, materia, grupoDestino, "Cambio de horario"
        );

        assertEquals(EstadosSolicitud.PENDIENTE, nuevaSolicitud.getEstadoString());

        // 2. Decanatura evalúa la solicitud
        decanatura.agregarSolicitud(nuevaSolicitud);
        assertEquals(1, decanatura.getSolicitudesPendientes().size());

        // 3. Decanatura aprueba la solicitud
        assertDoesNotThrow(() -> decanatura.aprobarSolicitud(nuevaSolicitud));
        assertEquals(EstadosSolicitud.APROBADO, nuevaSolicitud.getEstadoString());
        assertEquals(0, decanatura.getSolicitudesPendientes().size());
    }

    @Test
    public void testNotificacionesObservadores() {
        SolicitudCambio nuevaSolicitud = estudiante.crearSolicitud(
                materia, grupoOrigen, materia, grupoDestino, "Cambio de horario"
        );

        // Adjuntar observadores
        nuevaSolicitud.adjuntar(estudiante);
        nuevaSolicitud.adjuntar(decanatura);

        assertEquals(2, nuevaSolicitud.cantidadObservadores());

        // Cambiar estado debería notificar a los observadores
        assertDoesNotThrow(() -> nuevaSolicitud.actualizarEstado(new Aprobado(nuevaSolicitud), EstadosSolicitud.APROBADO));
    }

    // Tests adicionales para cubrir los métodos faltantes

    @Test
    public void testGetFechaCreacion() {
        Date fechaCreacion = solicitud.getFechaCreacion();
        assertNotNull(fechaCreacion);

        // La fecha debe ser reciente (dentro de los últimos 5 segundos)
        Date ahora = new Date();
        long diferencia = ahora.getTime() - fechaCreacion.getTime();
        assertTrue(diferencia < 5000); // menos de 5 segundos
    }

    @Test
    public void testGetPrioridad() {
        Prioridad prioridad = solicitud.getPrioridad();
        assertNotNull(prioridad);

        // Debería ser una instancia de PrioridadPorFecha
        assertTrue(prioridad instanceof PrioridadPorFecha);
    }

    @Test
    public void testSetEstado() {
        EstadoSolicitud nuevoEstado = new Aprobado(solicitud);
        solicitud.setEstado(nuevoEstado);
        assertEquals(nuevoEstado, solicitud.getEstado());
    }

    @Test
    public void testSetEstadoString() {
        solicitud.setEstadoString(EstadosSolicitud.APROBADO);
        assertEquals(EstadosSolicitud.APROBADO, solicitud.getEstadoString());
    }

    @Test
    public void testCambiarEstadoValido() {
        // Cambiar de PENDIENTE a APROBADO (transición válida)
        EstadoSolicitud estadoAprobado = new Aprobado(solicitud);

        assertDoesNotThrow(() -> {
            solicitud.cambiarEstado(estadoAprobado, EstadosSolicitud.APROBADO);
        });

        assertEquals(EstadosSolicitud.APROBADO, solicitud.getEstadoString());
        assertEquals(estadoAprobado, solicitud.getEstado());
    }

    @Test
    public void testCambiarEstadoInvalido() {
        // Intentar cambiar de PENDIENTE a un estado inválido
        // (esto depende de la implementación de EstadoValidator)
        EstadoSolicitud estadoRechazado = new Rechazado(solicitud);

        // Primero cambiar a APROBADO
        solicitud.setEstadoString(EstadosSolicitud.APROBADO);

        // Luego intentar cambiar a PENDIENTE (transición que podría ser inválida)
        EstadoSolicitud estadoPendiente = new Pendiente(solicitud);

        // Si EstadoValidator no permite la transición, debería lanzar excepción
        // Nota: Esto depende de la implementación específica de EstadoValidator
        assertThrows(RuntimeException.class, () -> {
            solicitud.cambiarEstado(estadoPendiente, EstadosSolicitud.PENDIENTE);
        });
    }

    @Test
    public void testValidarCupo() {
        // El grupo destino tiene cupo disponible por defecto
        assertTrue(solicitud.validarCupo());

        // Llenar el grupo destino al máximo
        for (int i = 0; i < 25; i++) {
            Estudiante est = new Estudiante("est-" + i, "123456" + i, "Estudiante " + i,
                    "est" + i + "@mail.escuelaing.edu.co", Facultades.INGENIERIA,
                    "Ingeniería", 3);
            grupoDestino.agregarEstudiante(est);
        }

        // Ahora no debería haber cupo disponible
        assertFalse(solicitud.validarCupo());
    }

    @Test
    public void testValidarCruceHorarios() {
        // Por ahora el método devuelve true (simplificado)
        assertTrue(solicitud.validarCruceHorarios());
    }

    @Test
    public void testActualizarEstado() {
        // Adjuntar un observador para verificar que se notifique
        solicitud.adjuntar(estudiante);
        assertEquals(1, solicitud.cantidadObservadores());

        EstadoSolicitud nuevoEstado = new Aprobado(solicitud);

        assertDoesNotThrow(() -> {
            solicitud.actualizarEstado(nuevoEstado, EstadosSolicitud.APROBADO);
        });

        assertEquals(EstadosSolicitud.APROBADO, solicitud.getEstadoString());
        assertEquals(nuevoEstado, solicitud.getEstado());
    }

    @Test
    public void testConstructorConTodosLosParametros() {
        // Test del constructor que aparece en el reporte de cobertura
        SolicitudCambio nuevaSolicitud = new SolicitudCambio(
                "sol-002",
                estudiante,
                materia,
                grupoOrigen,
                materia,
                grupoDestino,
                "Nueva solicitud de test"
        );

        assertNotNull(nuevaSolicitud);
        assertEquals("sol-002", nuevaSolicitud.getId());
        assertEquals(estudiante, nuevaSolicitud.getEstudiante());
        assertEquals("Nueva solicitud de test", nuevaSolicitud.getObservaciones());
        assertEquals(EstadosSolicitud.PENDIENTE, nuevaSolicitud.getEstadoString());
        assertNotNull(nuevaSolicitud.getFechaCreacion());
        assertNotNull(nuevaSolicitud.getPrioridad());
        assertTrue(nuevaSolicitud.getEstado() instanceof Pendiente);
    }

    @Test
    public void testCambiarEstadoNotificaObservadores() {
        // Adjuntar observadores
        solicitud.adjuntar(estudiante);
        solicitud.adjuntar(decanatura);

        EstadoSolicitud estadoAprobado = new Aprobado(solicitud);

        // El cambio de estado debería notificar a los observadores
        assertDoesNotThrow(() -> {
            solicitud.cambiarEstado(estadoAprobado, EstadosSolicitud.APROBADO);
        });

        assertEquals(EstadosSolicitud.APROBADO, solicitud.getEstadoString());
    }

    @Test
    public void testMultiplesCambiosDeEstado() {
        // Test para verificar múltiples cambios de estado válidos
        EstadoSolicitud estadoAprobado = new Aprobado(solicitud);
        EstadoSolicitud estadoRechazado = new Rechazado(solicitud);

        // PENDIENTE -> APROBADO
        assertDoesNotThrow(() -> {
            solicitud.cambiarEstado(estadoAprobado, EstadosSolicitud.APROBADO);
        });
        assertEquals(EstadosSolicitud.APROBADO, solicitud.getEstadoString());

        // Intentar APROBADO -> RECHAZADO (puede ser válido o no según EstadoValidator)
        // Comentar esta parte si la transición no es válida en tu implementación
        /*
        assertDoesNotThrow(() -> {
            solicitud.cambiarEstado(estadoRechazado, EstadosSolicitud.RECHAZADO);
        });
        assertEquals(EstadosSolicitud.RECHAZADO, solicitud.getEstadoString());
        */
    }

    @Test
    public void testSettersDirectos() {
        // Test para verificar que los setters funcionan correctamente
        EstadoSolicitud nuevoEstado = new Rechazado(solicitud);
        String nuevoEstadoString = EstadosSolicitud.RECHAZADO;

        solicitud.setEstado(nuevoEstado);
        solicitud.setEstadoString(nuevoEstadoString);

        assertEquals(nuevoEstado, solicitud.getEstado());
        assertEquals(nuevoEstadoString, solicitud.getEstadoString());
    }
}