package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.PeriodoAcademico;
import eci.edu.dosw.proyecto.service.interfaces.PeriodoAcademicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/periodos-academicos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PeriodoAcademicoEstadoController {

    private final PeriodoAcademicoService periodoAcademicoService;

    @GetMapping("/activo")
    public ResponseEntity<?> obtenerPeriodoActivo() {
        Optional<PeriodoAcademico> periodoActivo = periodoAcademicoService.obtenerPeriodoActivo();

        if (periodoActivo.isPresent()) {
            return ResponseEntity.ok(Collections.singletonMap("periodo", periodoActivo.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "No hay período académico activo"));
        }
    }

    @GetMapping("/solicitudes-activo")
    public ResponseEntity<?> obtenerPeriodoSolicitudesActivo() {
        Optional<PeriodoAcademico> periodoSolicitudesActivo = periodoAcademicoService.obtenerPeriodoSolicitudesActivo();

        if (periodoSolicitudesActivo.isPresent()) {
            return ResponseEntity.ok(Collections.singletonMap("periodo", periodoSolicitudesActivo.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "No hay período de solicitudes activo"));
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPeriodosPorEstado(@PathVariable String estado) {
        try {
            List<PeriodoAcademico> periodos = periodoAcademicoService.obtenerPeriodosPorEstado(estado);
            return ResponseEntity.ok(Collections.singletonMap("periodos", periodos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Estado no válido: " + estado));
        }
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarPeriodo(@PathVariable String id) {
        try {
            PeriodoAcademico periodoActivado = periodoAcademicoService.activarPeriodo(id);
            return ResponseEntity.ok(Collections.singletonMap("periodo", periodoActivado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/verificar/activo")
    public ResponseEntity<?> verificarPeriodoActivo() {
        boolean existeActivo = periodoAcademicoService.existePeriodoActivo();
        return ResponseEntity.ok(Collections.singletonMap("existePeriodoActivo", existeActivo));
    }

    @GetMapping("/verificar/solicitudes-activo")
    public ResponseEntity<?> verificarPeriodoSolicitudesActivo() {
        Optional<PeriodoAcademico> periodoSolicitudes = periodoAcademicoService.obtenerPeriodoSolicitudesActivo();
        boolean solicitudesActivas = periodoSolicitudes.isPresent();

        return ResponseEntity.ok(Collections.singletonMap("solicitudesActivas", solicitudesActivas));
    }
}