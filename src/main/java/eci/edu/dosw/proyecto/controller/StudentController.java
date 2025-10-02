package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Estudiante;
import eci.edu.dosw.proyecto.model.RolUsuario;
import eci.edu.dosw.proyecto.model.SemaforoAcademico;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<?> crearEstudiante(@Valid @RequestBody Estudiante estudiante) {
        try {
            // Valores por defecto
            estudiante.setPassword("passwordTemporal");
            estudiante.setRol(RolUsuario.ESTUDIANTE);
            estudiante.setActivo(true);
            estudiante.setFechaCreacion(LocalDateTime.now());
            estudiante.setPromedioAcumulado(0.0);
            estudiante.setSemaforo(SemaforoAcademico.AZUL);

            Estudiante estudianteCreado = studentService.crearEstudiante(estudiante);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("estudiante", estudianteCreado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodosLosEstudiantes() {
        List<Estudiante> estudiantes = studentService.obtenerTodosLosEstudiantes();
        return ResponseEntity.ok(Collections.singletonMap("estudiantes", estudiantes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEstudiantePorId(@PathVariable String id) {
        try {
            Estudiante estudiante = studentService.obtenerEstudiantePorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));
            return ResponseEntity.ok(Collections.singletonMap("estudiante", estudiante));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstudiante(@PathVariable String id, @Valid @RequestBody Estudiante estudiante) {
        try {
            estudiante.setId(id);
            Estudiante estudianteActualizado = studentService.actualizarEstudiante(estudiante);
            return ResponseEntity.ok(Collections.singletonMap("estudiante", estudianteActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}