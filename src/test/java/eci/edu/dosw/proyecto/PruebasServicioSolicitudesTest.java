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
 * Pruebas unitarias para el servicio de solicitudes - VERSIÓN FINAL CORREGIDA
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

    // CAMBIO IMPORTANTE: Hacer que solicitud sea un Mock
    @Mock
    private SolicitudCambio solicitud;

    @BeforeEach
    public void configuracionInicial() {
        // Configurar datos de prueba (objetos reales)
        estudiante = new Estudiante("est-001", "1001234567", "María García",
                "maria.garcia@mail.escuelaing.edu.co", "INGENIERIA", "Ingeniería de Sistemas", 3);

        materiaOrigen = new Materia("MAT101", "Matemáticas Básicas", 3, "INGENIERIA");
        materiaDestino = new Materia("PROG201", "Programación I", 4, "INGENIERIA");

        grupoDestino = new Grupo("G1", materiaDestino, "Prof. García", "Lunes 8-10", 30);

        // NO crear solicitud real aquí - ya es un Mock declarado con @Mock
    }

    @Test
    public void testCrearSolicitudExitoso() {
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

        // Act - Ejecución
        SolicitudResponse respuesta = servicioSolicitudes.crearSolicitud(solicitudRequest);

        // Assert - Verificación
        assertNotNull(respuesta, "La respuesta no debería ser nula");
        assertEquals("María García", respuesta.getNombreEstudiante());
        verify(gestorSistema).agregarSolicitud(any(SolicitudCambio.class));
    }

    @Test
    public void testAprobarSolicitud() {
        // Arrange - Crear mock de decanatura
        Decanatura decanoMock = mock(Decanatura.class);

        // Configurar el mock de solicitud
        when(gestorSistema.obtenerSolicitudPorId("solicitud-test-001")).thenReturn(solicitud);
        when(gestorSistema.obtenerDecanoPorFacultad("INGENIERIA")).thenReturn(decanoMock);

        // SOLUCIÓN: Configurar todos los métodos del mock solicitud
        when(solicitud.getMateriaOrigen()).thenReturn(materiaOrigen);
        when(solicitud.getEstadoString()).thenReturn("APROBADO");
        when(solicitud.getEstudiante()).thenReturn(estudiante);
        when(solicitud.getId()).thenReturn("solicitud-test-001");
        when(solicitud.getMateriaDestino()).thenReturn(materiaDestino);
        when(solicitud.getFechaCreacion()).thenReturn(new java.util.Date());
        when(solicitud.getObservaciones()).thenReturn("Conflicto de horarios");

        // Mock para prioridad
        Prioridad prioridadMock = mock(Prioridad.class);
        when(solicitud.getPrioridad()).thenReturn(prioridadMock);
        when(prioridadMock.calcularPrioridad()).thenReturn(895);

        // Act
        SolicitudResponse respuesta = servicioSolicitudes.aprobarSolicitud("solicitud-test-001");

        // Assert
        assertNotNull(respuesta);
        assertEquals("APROBADO", respuesta.getEstado());
        assertEquals("María García", respuesta.getNombreEstudiante());
        verify(decanoMock).aprobarSolicitud(solicitud);
    }

    @Test
    public void testObtenerSolicitudesPorEstado() {
        // Arrange
        when(gestorSistema.obtenerTodasSolicitudes()).thenReturn(List.of(solicitud));

        // SOLUCIÓN: Configurar todos los métodos necesarios del mock solicitud
        when(solicitud.getEstadoString()).thenReturn("PENDIENTE");
        when(solicitud.getId()).thenReturn("solicitud-test-001");
        when(solicitud.getEstudiante()).thenReturn(estudiante);
        when(solicitud.getMateriaOrigen()).thenReturn(materiaOrigen);
        when(solicitud.getMateriaDestino()).thenReturn(materiaDestino);
        when(solicitud.getFechaCreacion()).thenReturn(new java.util.Date());
        when(solicitud.getObservaciones()).thenReturn("Conflicto de horarios");

        // Mock para prioridad
        Prioridad prioridadMock = mock(Prioridad.class);
        when(solicitud.getPrioridad()).thenReturn(prioridadMock);
        when(prioridadMock.calcularPrioridad()).thenReturn(895);

        // Act
        List<SolicitudResponse> respuestas = servicioSolicitudes.obtenerSolicitudesPorEstado("PENDIENTE");

        // Assert
        assertNotNull(respuestas);
        assertEquals(1, respuestas.size());
        assertEquals("solicitud-test-001", respuestas.get(0).getId());
        assertEquals("PENDIENTE", respuestas.get(0).getEstado());
        assertEquals("María García", respuestas.get(0).getNombreEstudiante());
    }
}