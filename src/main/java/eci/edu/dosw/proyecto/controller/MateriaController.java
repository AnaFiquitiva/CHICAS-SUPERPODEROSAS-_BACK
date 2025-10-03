package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Materia;
import eci.edu.dosw.proyecto.service.interfaces.MateriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/materias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MateriaController {

    private final MateriaService materiaService;

    @PostMapping
    public ResponseEntity<?> crearMateria(@Valid @RequestBody Materia materia) {
        try {
            Materia materiaCreada = materiaService.crearMateria(materia);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("materia", materiaCreada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodasLasMaterias() {
        List<Materia> materias = materiaService.obtenerTodasLasMaterias();
        return ResponseEntity.ok(Collections.singletonMap("materias", materias));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMateriaPorId(@PathVariable String id) {
        try {
            Materia materia = materiaService.obtenerMateriaPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada"));
            return ResponseEntity.ok(Collections.singletonMap("materia", materia));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerMateriaPorCodigo(@PathVariable String codigo) {
        try {
            Materia materia = materiaService.obtenerMateriaPorCodigo(codigo)
                    .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada con código: " + codigo));
            return ResponseEntity.ok(Collections.singletonMap("materia", materia));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMateria(@PathVariable String id, @Valid @RequestBody Materia materia) {
        try {
            materia.setId(id);
            Materia materiaActualizada = materiaService.actualizarMateria(materia);
            return ResponseEntity.ok(Collections.singletonMap("materia", materiaActualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMateria(@PathVariable String id) {
        try {
            materiaService.eliminarMateria(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Materia eliminada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}