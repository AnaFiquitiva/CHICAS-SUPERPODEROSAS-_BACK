package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Solicitud;
import eci.edu.dosw.proyecto.service.interfaces.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudService solicitudService;

    @PostMapping
    public ResponseEntity<?> crearSolicitud(@Valid @RequestBody Solicitud solicitud) {
        try {
            Solicitud solicitudCreada = solicitudService.crearSolicitud(solicitud);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("solicitud", solicitudCreada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodasLasSolicitudes() {
        List<Solicitud> solicitudes = solicitudService.obtenerTodasLasSolicitudes();
        return ResponseEntity.ok(Collections.singletonMap("solicitudes", solicitudes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSolicitudPorId(@PathVariable String id) {
        try {
            Solicitud solicitud = solicitudService.obtenerSolicitudPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
            return ResponseEntity.ok(Collections.singletonMap("solicitud", solicitud));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSolicitud(@PathVariable String id, @Valid @RequestBody Solicitud solicitud) {
        try {
            solicitud.setId(id);
            Solicitud solicitudActualizada = solicitudService.actualizarSolicitud(solicitud);
            return ResponseEntity.ok(Collections.singletonMap("solicitud", solicitudActualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSolicitud(@PathVariable String id) {
        try {
            solicitudService.eliminarSolicitud(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Solicitud eliminada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}