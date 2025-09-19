package eci.edu.dosw.proyecto;

import eci.edu.dosw.proyecto.dto.SolicitudRequest;
import eci.edu.dosw.proyecto.dto.SolicitudResponse;
import eci.edu.dosw.proyecto.model.*;

import eci.edu.dosw.proyecto.servicio.GestorSistema;
import eci.edu.dosw.proyecto.servicio.ServicioSolicitudesImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de solicitudes
 */
@ExtendWith(MockitoExtension.class)
public class PruebasServicioSolicitudesTest {

    @Mock
    private GestorSistema gestorSistema;

    @InjectMocks
    private ServicioSolicitudesImpl servicioSolicitudes;

    private Estudiante estudiante;
    private Materia materiaOrigen;
    private Materia materiaDestino;
    private Grupo grupoDestino;
    private SolicitudCambio solicitud;

    @BeforeEach
    void configuracionInicial() {
        // Configurar datos de prueba
        estudiante = new Estudiante("est-001", "1001234567", "María García",
                "maria.garcia@mail.escuelaing.edu.co", "INGENIERIA", "Ingeniería de Sistemas", 3);

        materiaOrigen = new Materia("MAT101", "Matemáticas Básicas", 3, "INGENIERIA");
        materiaDestino = new Materia("PROG201", "Programación I", 4, "INGENIERIA");

        grupoDestino = new Grupo("G1", materiaDestino, "Prof. García", "Lunes 8-10", 30);

        solicitud = mock(SolicitudCambio.class);
    }

    @Test
    void testCrearSolicitudExitoso() {
        // Arrange - Configuración
        SolicitudRequest solicitudRequest = new SolicitudRequest();
        solicitudRequest.setIdEstudiante("1001234567");
        solicitudRequest.setCodigoMateriaOrigen("MAT101");
        solicitudRequest.setCodigoMateriaDestino("PROG201");
        solicitudRequest.setCodigoGrupoDestino("G1");
        solicitudRequest.setObservaciones("Conflicto de horarios");

        when(gestorSistema.obtenerEstudiantePorId("1001234567")).thenReturn(estudiante);
        when(gestorSistema.obtenerMateriaPorCodigo("MAT101")).thenReturn(materiaOrigen);
        when(gestorSistema.obtenerMateriaPorCodigo("PROG201")).thenReturn(materiaDestino);
        when(gestorSistema.obtenerGrupoPorCodigo("G1")).thenReturn(grupoDestino);
        when(estudiante.crearSolicitud(any(), any(), any(), any(), any())).thenReturn(solicitud);
        when(solicitud.getId()).thenReturn("solicitud-test-001");
        when(solicitud.getEstadoString()).thenReturn("PENDIENTE");
        when(solicitud.getFechaCreacion()).thenReturn(new java.util.Date());
        when(solicitud.getObservaciones()).thenReturn("Conflicto de horarios");
        when(solicitud.getPrioridad()).thenReturn(mock(Prioridad.class));
        when(solicitud.getPrioridad().calcularPrioridad()).thenReturn(895);

        // Act - Ejecución
        SolicitudResponse respuesta = servicioSolicitudes.crearSolicitud(solicitudRequest);

        // Assert - Verificación
        assertNotNull(respuesta, "La respuesta no debería ser nula");
        assertEquals("solicitud-test-001", respuesta.getId(), "El ID de la solicitud debería coincidir");
        assertEquals("PENDIENTE", respuesta.getEstado(), "El estado debería ser PENDIENTE");
        assertEquals(895, respuesta.getPrioridad(), "La prioridad debería coincidir");

        verify(gestorSistema).agregarSolicitud(solicitud);
    }

    @Test
    void testAprobarSolicitud() {
        // Arrange
        Decanatura decano = mock(Decanatura.class);
        when(gestorSistema.obtenerSolicitudPorId("solicitud-test-001")).thenReturn(solicitud);
        when(solicitud.getMateriaOrigen()).thenReturn(materiaOrigen);
        when(gestorSistema.obtenerDecanoPorFacultad("INGENIERIA")).thenReturn(decano);
        when(solicitud.getEstadoString()).thenReturn("APROBADO");
        when(solicitud.getEstudiante()).thenReturn(estudiante);
        when(estudiante.getNombre()).thenReturn("María García");
        when(solicitud.getMateriaOrigen()).thenReturn(materiaOrigen);
        when(materiaOrigen.getNombre()).thenReturn("Matemáticas Básicas");
        when(solicitud.getMateriaDestino()).thenReturn(materiaDestino);
        when(materiaDestino.getNombre()).thenReturn("Programación I");
        when(solicitud.getFechaCreacion()).thenReturn(new java.util.Date());
        when(solicitud.getObservaciones()).thenReturn("Conflicto de horarios");
        when(solicitud.getPrioridad()).thenReturn(mock(Prioridad.class));
        when(solicitud.getPrioridad().calcularPrioridad()).thenReturn(895);

        // Act
        SolicitudResponse respuesta = servicioSolicitudes.aprobarSolicitud("solicitud-test-001");

        // Assert
        assertNotNull(respuesta);
        assertEquals("APROBADO", respuesta.getEstado());
        assertEquals("María García", respuesta.getNombreEstudiante());
        verify(decano).aprobarSolicitud(solicitud);
    }

    @Test
    void testObtenerSolicitudesPorEstado() {
        // Arrange
        when(gestorSistema.obtenerTodasSolicitudes()).thenReturn(List.of(solicitud));
        when(solicitud.getEstadoString()).thenReturn("PENDIENTE");
        when(solicitud.getId()).thenReturn("solicitud-test-001");
        when(solicitud.getEstudiante()).thenReturn(estudiante);
        when(estudiante.getNombre()).thenReturn("María García");
        when(solicitud.getMateriaOrigen()).thenReturn(materiaOrigen);
        when(materiaOrigen.getNombre()).thenReturn("Matemáticas Básicas");
        when(solicitud.getMateriaDestino()).thenReturn(materiaDestino);
        when(materiaDestino.getNombre()).thenReturn("Programación I");
        when(solicitud.getFechaCreacion()).thenReturn(new java.util.Date());
        when(solicitud.getObservaciones()).thenReturn("Conflicto de horarios");
        when(solicitud.getPrioridad()).thenReturn(mock(Prioridad.class));
        when(solicitud.getPrioridad().calcularPrioridad()).thenReturn(895);

        // Act
        List<SolicitudResponse> respuestas = servicioSolicitudes.obtenerSolicitudesPorEstado("PENDIENTE");

        // Assert
        assertNotNull(respuestas);
        assertEquals(1, respuestas.size());
        assertEquals("solicitud-test-001", respuestas.get(0).getId());
        assertEquals("PENDIENTE", respuestas.get(0).getEstado());
    }
}