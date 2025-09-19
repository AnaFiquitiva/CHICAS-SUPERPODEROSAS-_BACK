package eci.edu.dosw.proyecto;


import eci.edu.dosw.proyecto.controlador.*;

import eci.edu.dosw.proyecto.dto.SolicitudRequest;
import eci.edu.dosw.proyecto.dto.SolicitudResponse;
import eci.edu.dosw.proyecto.servicio.ServicioSolicitudes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ControladorSolicitudesTest {

    @Mock
    private ServicioSolicitudes servicioSolicitudes;

    @InjectMocks
    private ControladorSolicitudes controladorSolicitudes;

    private SolicitudRequest solicitudRequest;
    private SolicitudResponse solicitudResponse;

    @BeforeEach
    public void setUp() {
        solicitudRequest = new SolicitudRequest();
        solicitudRequest.setIdEstudiante("1001234567");
        solicitudRequest.setCodigoMateriaOrigen("MAT101");
        solicitudRequest.setCodigoMateriaDestino("PROG201");
        solicitudRequest.setCodigoGrupoDestino("G2");

        solicitudResponse = new SolicitudResponse();
        solicitudResponse.setId("test-id");
        solicitudResponse.setNombreEstudiante("María García");
        solicitudResponse.setEstado("PENDIENTE");
    }

    @Test
    public void testCrearSolicitud() {
        when(servicioSolicitudes.crearSolicitud(any(SolicitudRequest.class)))
                .thenReturn(solicitudResponse);

        ResponseEntity<SolicitudResponse> response =
                controladorSolicitudes.crearSolicitud(solicitudRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("test-id", response.getBody().getId());
    }

    @Test
    public void testAprobarSolicitud() {
        when(servicioSolicitudes.aprobarSolicitud("test-id"))
                .thenReturn(solicitudResponse);

        ResponseEntity<SolicitudResponse> response =
                controladorSolicitudes.aprobarSolicitud("test-id");

        assertEquals(200, response.getStatusCodeValue());
        verify(servicioSolicitudes).aprobarSolicitud("test-id");
    }

    @Test
    public void testRechazarSolicitud() {
        when(servicioSolicitudes.rechazarSolicitud("test-id", "Motivo test"))
                .thenReturn(solicitudResponse);

        ResponseEntity<SolicitudResponse> response =
                controladorSolicitudes.rechazarSolicitud("test-id", "Motivo test");

        assertEquals(200, response.getStatusCodeValue());
        verify(servicioSolicitudes).rechazarSolicitud("test-id", "Motivo test");
    }

    @Test
    public void testObtenerSolicitudesPorEstado() {
        when(servicioSolicitudes.obtenerSolicitudesPorEstado("PENDIENTE"))
                .thenReturn(List.of(solicitudResponse));

        ResponseEntity<List<SolicitudResponse>> response =
                controladorSolicitudes.obtenerSolicitudesPorEstado("PENDIENTE");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testObtenerSolicitudesPorEstudiante() {
        when(servicioSolicitudes.obtenerSolicitudesPorEstudiante("1001234567"))
                .thenReturn(List.of(solicitudResponse));

        ResponseEntity<List<SolicitudResponse>> response =
                controladorSolicitudes.obtenerSolicitudesPorEstudiante("1001234567");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testObtenerSolicitudPorId() {
        when(servicioSolicitudes.obtenerSolicitudPorId("test-id"))
                .thenReturn(solicitudResponse);

        ResponseEntity<SolicitudResponse> response =
                controladorSolicitudes.obtenerSolicitudPorId("test-id");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test-id", response.getBody().getId());
    }

    @Test
    public void testObtenerTodasLasSolicitudes() {
        when(servicioSolicitudes.obtenerTodasLasSolicitudes())
                .thenReturn(List.of(solicitudResponse));

        ResponseEntity<List<SolicitudResponse>> response =
                controladorSolicitudes.obtenerTodasLasSolicitudes();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
}