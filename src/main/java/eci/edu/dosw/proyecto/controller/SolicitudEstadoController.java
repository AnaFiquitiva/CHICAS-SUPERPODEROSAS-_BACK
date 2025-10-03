package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Solicitud;
import eci.edu.dosw.proyecto.model.EstadoSolicitud;
import eci.edu.dosw.proyecto.service.interfaces.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitudEstadoController {

    private final SolicitudService solicitudService;

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoSolicitud(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(request.get("estado"));
            String motivo = request.get("motivo");

            Solicitud solicitudActualizada = solicitudService.cambiarEstadoSolicitud(id, nuevoEstado, motivo);
            return ResponseEntity.ok(Collections.singletonMap("solicitud", solicitudActualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerSolicitudesPorEstado(@PathVariable String estado) {
        try {
            EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(estado.toUpperCase());
            List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesPorEstado(estadoEnum);
            return ResponseEntity.ok(Collections.singletonMap("solicitudes", solicitudes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Estado no válido: " + estado));
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> obtenerSolicitudesPendientes() {
        List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesPendientes();
        return ResponseEntity.ok(Collections.singletonMap("solicitudes", solicitudes));
    }

    @GetMapping("/vencidas")
    public ResponseEntity<?> obtenerSolicitudesVencidas() {
        List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesVencidas();
        return ResponseEntity.ok(Collections.singletonMap("solicitudes", solicitudes));
    }
}