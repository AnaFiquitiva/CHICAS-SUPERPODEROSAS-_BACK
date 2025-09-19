package eci.edu.dosw.proyecto.controlador;

import eci.edu.dosw.proyecto.dto.SolicitudRequest;
import eci.edu.dosw.proyecto.dto.SolicitudResponse;
import eci.edu.dosw.proyecto.servicio.ServicioSolicitudes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de solicitudes de cambio de horario
 * Proporciona endpoints para crear, aprobar, rechazar y consultar solicitudes
 */
@RestController
@RequestMapping("/api/solicitudes")
public class ControladorSolicitudes {

    private final ServicioSolicitudes servicioSolicitudes;

    public ControladorSolicitudes(ServicioSolicitudes servicioSolicitudes) {
        this.servicioSolicitudes = servicioSolicitudes;
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Estudiante, materia o grupo no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SolicitudResponse> crearSolicitud(
            @Parameter(description = "Datos de la solicitud a crear", required = true)
            @Valid @RequestBody SolicitudRequest solicitudRequest) {

        SolicitudResponse response = servicioSolicitudes.crearSolicitud(solicitudRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/aprobar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud aprobada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada"),
            @ApiResponse(responseCode = "400", description = "No se puede aprobar la solicitud en su estado actual")
    })
    public ResponseEntity<SolicitudResponse> aprobarSolicitud(
            @Parameter(description = "ID de la solicitud a aprobar", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {

        SolicitudResponse response = servicioSolicitudes.aprobarSolicitud(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/rechazar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud rechazada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    public ResponseEntity<SolicitudResponse> rechazarSolicitud(
            @Parameter(description = "ID de la solicitud a rechazar", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id,

            @Parameter(description = "Motivo del rechazo de la solicitud", required = true, example = "Cupo insuficiente en el grupo destino")
            @RequestParam String motivo) {

        SolicitudResponse response = servicioSolicitudes.rechazarSolicitud(id, motivo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/estado/{estado}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes obtenida exitosamente")
    })
    public ResponseEntity<List<SolicitudResponse>> obtenerSolicitudesPorEstado(
            @Parameter(description = "Estado de las solicitudes a filtrar", required = true, example = "PENDIENTE")
            @PathVariable String estado) {

        List<SolicitudResponse> responses = servicioSolicitudes.obtenerSolicitudesPorEstado(estado);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/estudiante/{idEstudiante}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes del estudiante obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    public ResponseEntity<List<SolicitudResponse>> obtenerSolicitudesPorEstudiante(
            @Parameter(description = "ID o código del estudiante", required = true, example = "1001234567")
            @PathVariable String idEstudiante) {

        List<SolicitudResponse> responses = servicioSolicitudes.obtenerSolicitudesPorEstudiante(idEstudiante);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    public ResponseEntity<SolicitudResponse> obtenerSolicitudPorId(
            @Parameter(description = "ID de la solicitud a consultar", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {

        SolicitudResponse response = servicioSolicitudes.obtenerSolicitudPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista completa de solicitudes obtenida exitosamente")
    })
    public ResponseEntity<List<SolicitudResponse>> obtenerTodasLasSolicitudes() {
        List<SolicitudResponse> responses = servicioSolicitudes.obtenerTodasLasSolicitudes();
        return ResponseEntity.ok(responses);
    }
}