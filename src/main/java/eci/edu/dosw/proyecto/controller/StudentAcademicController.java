package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Estudiante;
import eci.edu.dosw.proyecto.model.SemaforoAcademico;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentAcademicController {

    private final StudentService studentService;

    @GetMapping("/carrera/{carrera}")
    public ResponseEntity<?> obtenerPorCarrera(@PathVariable String carrera) {
        List<Estudiante> estudiantes = studentService.obtenerEstudiantesPorCarrera(carrera);
        return ResponseEntity.ok(Collections.singletonMap("estudiantes", estudiantes));
    }

    @GetMapping("/semestre/{semestre}")
    public ResponseEntity<?> obtenerPorSemestre(@PathVariable Integer semestre) {
        List<Estudiante> estudiantes = studentService.obtenerEstudiantesPorSemestre(semestre);
        return ResponseEntity.ok(Collections.singletonMap("estudiantes", estudiantes));
    }

    @GetMapping("/semaforo/{semaforo}")
    public ResponseEntity<?> obtenerPorSemaforo(@PathVariable String semaforo) {
        try {
            SemaforoAcademico semaforoEnum = SemaforoAcademico.valueOf(semaforo.toUpperCase());
            List<Estudiante> estudiantes = studentService.obtenerEstudiantesPorSemaforo(semaforoEnum);
            return ResponseEntity.ok(Collections.singletonMap("estudiantes", estudiantes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Semáforo no válido: " + semaforo));
        }
    }

    @PatchMapping("/{id}/semaforo")
    public ResponseEntity<?> actualizarSemaforo(@PathVariable String id, @RequestParam Double promedio) {
        try {
            Estudiante estudianteActualizado = studentService.actualizarSemaforo(id, promedio);
            return ResponseEntity.ok(Collections.singletonMap("estudiante", estudianteActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}