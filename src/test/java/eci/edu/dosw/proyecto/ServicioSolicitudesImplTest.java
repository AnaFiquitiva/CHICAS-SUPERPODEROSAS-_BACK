package eci.edu.dosw.proyecto;
import eci.edu.dosw.proyecto.servicio.*;


import eci.edu.dosw.proyecto.dto.SolicitudRequest;
import eci.edu.dosw.proyecto.dto.SolicitudResponse;
import eci.edu.dosw.proyecto.model.*;
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

@ExtendWith(MockitoExtension.class)
public class ServicioSolicitudesImplTest {

    @Mock
    private GestorSistema gestorSistema;

    @InjectMocks
    private ServicioSolicitudesImpl servicioSolicitudes;

    private Estudiante estudiante;
    private Materia materiaOrigen;
    private Materia materiaDestino;
    private Grupo grupoOrigen;
    private Grupo grupoDestino;
    private Decanatura decano;
    private SolicitudCambio solicitud;

    @BeforeEach
    public void setUp() {
        estudiante = new Estudiante("est-001", "1001234567", "María García",
                "maria@test.com", "INGENIERIA", "Sistemas", 3);

        materiaOrigen = new Materia("MAT101", "Matemáticas", 3, "INGENIERIA");
        materiaDestino = new Materia("PROG201", "Programación", 4, "INGENIERIA");

        grupoOrigen = new Grupo("G1", materiaOrigen, "Profesor", "Lunes", 30);
        grupoDestino = new Grupo("G2", materiaDestino, "Profesor", "Martes", 25);

        // Crear mock en lugar de objeto real
        decano = mock(Decanatura.class);

        // Crear solicitud usando el método del estudiante
        solicitud = estudiante.crearSolicitud(
                materiaOrigen,
                grupoOrigen,
                materiaDestino,
                grupoDestino,
                "Test observaciones"
        );
    }


    @Test
    public void testCrearSolicitudExitosamente() {
        // Configurar mocks
        when(gestorSistema.obtenerEstudiantePorId("1001234567")).thenReturn(estudiante);
        when(gestorSistema.obtenerMateriaPorCodigo("MAT101")).thenReturn(materiaOrigen);
        when(gestorSistema.obtenerMateriaPorCodigo("PROG201")).thenReturn(materiaDestino);
        when(gestorSistema.obtenerGrupoPorCodigo("G1")).thenReturn(grupoOrigen);
        when(gestorSistema.obtenerGrupoPorCodigo("G2")).thenReturn(grupoDestino);

        // Crear request
        SolicitudRequest request = new SolicitudRequest();
        request.setIdEstudiante("1001234567");
        request.setCodigoMateriaOrigen("MAT101");
        request.setCodigoGrupoOrigen("G1");
        request.setCodigoMateriaDestino("PROG201");
        request.setCodigoGrupoDestino("G2");
        request.setObservaciones("Test observaciones");

        // Ejecutar
        SolicitudResponse response = servicioSolicitudes.crearSolicitud(request);

        // Verificar
        assertNotNull(response);
        assertEquals("María García", response.getNombreEstudiante());
        verify(gestorSistema).agregarSolicitud(any(SolicitudCambio.class));
    }

    @Test
    public void testCrearSolicitudSinGrupoOrigen() {
        when(gestorSistema.obtenerEstudiantePorId("1001234567")).thenReturn(estudiante);
        when(gestorSistema.obtenerMateriaPorCodigo("MAT101")).thenReturn(materiaOrigen);
        when(gestorSistema.obtenerMateriaPorCodigo("PROG201")).thenReturn(materiaDestino);
        when(gestorSistema.obtenerGrupoPorCodigo("G2")).thenReturn(grupoDestino);

        SolicitudRequest request = new SolicitudRequest();
        request.setIdEstudiante("1001234567");
        request.setCodigoMateriaOrigen("MAT101");
        request.setCodigoMateriaDestino("PROG201");
        request.setCodigoGrupoDestino("G2");

        SolicitudResponse response = servicioSolicitudes.crearSolicitud(request);

        assertNotNull(response);
        verify(gestorSistema).agregarSolicitud(any(SolicitudCambio.class));
    }

    @Test
    public void testCrearSolicitudConError() {
        when(gestorSistema.obtenerEstudiantePorId("1001234567"))
                .thenThrow(new RuntimeException("Estudiante no encontrado"));

        SolicitudRequest request = new SolicitudRequest();
        request.setIdEstudiante("1001234567");

        assertThrows(RuntimeException.class, () -> {
            servicioSolicitudes.crearSolicitud(request);
        });
    }


    @Test
    public void testAprobarSolicitud() {
        when(gestorSistema.obtenerSolicitudPorId(anyString())).thenReturn(solicitud);
        when(gestorSistema.obtenerDecanoPorFacultad("INGENIERIA")).thenReturn(decano);

        SolicitudResponse response = servicioSolicitudes.aprobarSolicitud("test-id");

        assertNotNull(response);
        verify(decano).aprobarSolicitud(solicitud); // Ahora funciona porque decano es mock
    }

    @Test
    public void testRechazarSolicitud() {
        when(gestorSistema.obtenerSolicitudPorId(anyString())).thenReturn(solicitud);
        when(gestorSistema.obtenerDecanoPorFacultad("INGENIERIA")).thenReturn(decano);

        SolicitudResponse response = servicioSolicitudes.rechazarSolicitud("test-id", "Motivo test");

        assertNotNull(response);
        verify(decano).rechazarSolicitud(solicitud, "Motivo test"); // Ahora funciona porque decano es mock
    }
    @Test
    public void testObtenerSolicitudesPorEstado() {
        when(gestorSistema.obtenerTodasSolicitudes()).thenReturn(List.of(solicitud));

        List<SolicitudResponse> responses = servicioSolicitudes.obtenerSolicitudesPorEstado("PENDIENTE");

        assertEquals(1, responses.size());
    }

    @Test
    public void testObtenerSolicitudesPorEstudiante() {
        when(gestorSistema.obtenerTodasSolicitudes()).thenReturn(List.of(solicitud));

        List<SolicitudResponse> responses = servicioSolicitudes.obtenerSolicitudesPorEstudiante("est-001");

        assertEquals(1, responses.size());
    }

    @Test
    public void testObtenerSolicitudPorId() {
        when(gestorSistema.obtenerSolicitudPorId(anyString())).thenReturn(solicitud);

        SolicitudResponse response = servicioSolicitudes.obtenerSolicitudPorId("test-id");

        assertNotNull(response);
        assertEquals("María García", response.getNombreEstudiante());
    }

    @Test
    public void testObtenerTodasLasSolicitudes() {
        when(gestorSistema.obtenerTodasSolicitudes()).thenReturn(List.of(solicitud));

        List<SolicitudResponse> responses = servicioSolicitudes.obtenerTodasLasSolicitudes();

        assertEquals(1, responses.size());
    }
}