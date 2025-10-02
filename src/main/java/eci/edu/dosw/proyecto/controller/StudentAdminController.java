package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Estudiante;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/admin/estudiantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentAdminController {

    private final StudentService studentService;

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            Estudiante estudiante = studentService.obtenerEstudiantePorCodigo(codigo)
                    .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));
            return ResponseEntity.ok(Collections.singletonMap("estudiante", estudiante));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEstudiante(@PathVariable String id) {
        try {
            studentService.eliminarEstudiante(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Estudiante eliminado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}