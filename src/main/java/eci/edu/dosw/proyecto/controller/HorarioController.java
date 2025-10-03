package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Horario;
import eci.edu.dosw.proyecto.model.DiaSemana;
import eci.edu.dosw.proyecto.service.interfaces.HorarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HorarioController {

    private final HorarioService horarioService;

    @PostMapping
    public ResponseEntity<?> crearHorario(@Valid @RequestBody Horario horario) {
        try {
            Horario horarioCreado = horarioService.crearHorario(horario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("horario", horarioCreado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodosLosHorarios() {
        List<Horario> horarios = horarioService.obtenerTodosLosHorarios();
        return ResponseEntity.ok(Collections.singletonMap("horarios", horarios));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerHorarioPorId(@PathVariable String id) {
        try {
            Horario horario = horarioService.obtenerHorarioPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
            return ResponseEntity.ok(Collections.singletonMap("horario", horario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarHorario(@PathVariable String id, @Valid @RequestBody Horario horario) {
        try {
            horario.setId(id);
            Horario horarioActualizado = horarioService.actualizarHorario(horario);
            return ResponseEntity.ok(Collections.singletonMap("horario", horarioActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarHorario(@PathVariable String id) {
        try {
            horarioService.eliminarHorario(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Horario eliminado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}