package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.PeriodoAcademico;
import eci.edu.dosw.proyecto.service.interfaces.PeriodoAcademicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/periodos-academicos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PeriodoAcademicoController {

    private final PeriodoAcademicoService periodoAcademicoService;

    @PostMapping
    public ResponseEntity<?> crearPeriodo(@Valid @RequestBody PeriodoAcademico periodo) {
        try {
            PeriodoAcademico periodoCreado = periodoAcademicoService.crearPeriodo(periodo);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("periodo", periodoCreado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodosLosPeriodos() {
        List<PeriodoAcademico> periodos = periodoAcademicoService.obtenerTodosLosPeriodos();
        return ResponseEntity.ok(Collections.singletonMap("periodos", periodos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPeriodoPorId(@PathVariable String id) {
        try {
            PeriodoAcademico periodo = periodoAcademicoService.obtenerPeriodoPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Período no encontrado"));
            return ResponseEntity.ok(Collections.singletonMap("periodo", periodo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPeriodo(@PathVariable String id, @Valid @RequestBody PeriodoAcademico periodo) {
        try {
            periodo.setId(id);
            PeriodoAcademico periodoActualizado = periodoAcademicoService.actualizarPeriodo(periodo);
            return ResponseEntity.ok(Collections.singletonMap("periodo", periodoActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPeriodo(@PathVariable String id) {
        try {
            periodoAcademicoService.eliminarPeriodo(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Período eliminado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}