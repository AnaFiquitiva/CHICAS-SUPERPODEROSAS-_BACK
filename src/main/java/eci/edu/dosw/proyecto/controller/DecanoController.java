package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.model.Decano;
import eci.edu.dosw.proyecto.model.RolUsuario;
import eci.edu.dosw.proyecto.service.interfaces.DecanoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/decanos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DecanoController {

    private final DecanoService decanoService;

    @PostMapping
    public ResponseEntity<?> crearDecano(@Valid @RequestBody Decano decano) {
        try {
            // Valores por defecto para el decano
            decano.setRol(RolUsuario.DECANO);
            decano.setActivo(true);
            decano.setFechaCreacion(LocalDateTime.now());

            Decano decanoCreado = decanoService.crearDecano(decano);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("decano", decanoCreado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodosLosDecanos() {
        List<Decano> decanos = decanoService.obtenerTodosLosDecanos();
        return ResponseEntity.ok(Collections.singletonMap("decanos", decanos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDecanoPorId(@PathVariable String id) {
        try {
            Decano decano = decanoService.obtenerDecanoPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Decano no encontrado"));
            return ResponseEntity.ok(Collections.singletonMap("decano", decano));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerDecanoPorCodigo(@PathVariable String codigo) {
        try {
            Decano decano = decanoService.obtenerDecanoPorCodigo(codigo)
                    .orElseThrow(() -> new IllegalArgumentException("Decano no encontrado con código: " + codigo));
            return ResponseEntity.ok(Collections.singletonMap("decano", decano));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/facultad/{facultad}")
    public ResponseEntity<?> obtenerDecanoPorFacultad(@PathVariable String facultad) {
        try {
            Decano decano = decanoService.obtenerDecanoPorFacultad(facultad)
                    .orElseThrow(() -> new IllegalArgumentException("No hay decano para la facultad: " + facultad));
            return ResponseEntity.ok(Collections.singletonMap("decano", decano));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDecano(@PathVariable String id, @Valid @RequestBody Decano decano) {
        try {
            decano.setId(id);
            Decano decanoActualizado = decanoService.actualizarDecano(decano);
            return ResponseEntity.ok(Collections.singletonMap("decano", decanoActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDecano(@PathVariable String id) {
        try {
            decanoService.eliminarDecano(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Decano eliminado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}