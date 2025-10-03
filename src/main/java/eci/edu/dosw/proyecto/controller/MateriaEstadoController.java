package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Materia;
import eci.edu.dosw.proyecto.service.interfaces.MateriaService;
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
public class MateriaEstadoController {

    private final MateriaService materiaService;

    @GetMapping("/activas")
    public ResponseEntity<?> obtenerMateriasActivas() {
        List<Materia> materias = materiaService.obtenerMateriasActivas();
        return ResponseEntity.ok(Collections.singletonMap("materias", materias));
    }

    @GetMapping("/facultad/{facultad}")
    public ResponseEntity<?> obtenerMateriasPorFacultad(@PathVariable String facultad) {
        List<Materia> materias = materiaService.obtenerMateriasPorFacultad(facultad);
        return ResponseEntity.ok(Collections.singletonMap("materias", materias));
    }

    @GetMapping("/semestre/{semestre}")
    public ResponseEntity<?> obtenerMateriasPorSemestre(@PathVariable Integer semestre) {
        List<Materia> materias = materiaService.obtenerMateriasPorSemestre(semestre);
        return ResponseEntity.ok(Collections.singletonMap("materias", materias));
    }

    @GetMapping("/cupos-disponibles")
    public ResponseEntity<?> obtenerMateriasConCuposDisponibles() {
        List<Materia> materias = materiaService.obtenerMateriasConCuposDisponibles();
        return ResponseEntity.ok(Collections.singletonMap("materias", materias));
    }

    @GetMapping("/electivas")
    public ResponseEntity<?> obtenerMateriasElectivas() {
        List<Materia> materias = materiaService.obtenerMateriasElectivas();
        return ResponseEntity.ok(Collections.singletonMap("materias", materias));
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarMateriasPorNombre(@RequestParam String nombre) {
        List<Materia> materias = materiaService.buscarMateriasPorNombre(nombre);
        return ResponseEntity.ok(Collections.singletonMap("materias", materias));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarMateria(@PathVariable String id) {
        try {
            Materia materiaActivada = materiaService.activarMateria(id);
            return ResponseEntity.ok(Collections.singletonMap("materia", materiaActivada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarMateria(@PathVariable String id) {
        try {
            Materia materiaDesactivada = materiaService.desactivarMateria(id);
            return ResponseEntity.ok(Collections.singletonMap("materia", materiaDesactivada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}