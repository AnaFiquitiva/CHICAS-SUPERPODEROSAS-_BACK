package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import eci.edu.dosw.proyecto.model.Estudiante;
import eci.edu.dosw.proyecto.model.SemaforoAcademico;
import eci.edu.dosw.proyecto.model.RolUsuario;
import eci.edu.dosw.proyecto.service.interfaces.EstudianteService;
import eci.edu.dosw.proyecto.dto.EstudianteDTO;
import eci.edu.dosw.proyecto.dto.EstudianteInfoDTO;
import eci.edu.dosw.proyecto.dto.EstudianteUpdateDTO;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @PostMapping
    public ResponseEntity<?> crearEstudiante(@Valid @RequestBody EstudianteDTO estudianteDTO) {
        try {
            Estudiante estudiante = Estudiante.builder()
                    .codigo(estudianteDTO.getCodigo())
                    .nombre(estudianteDTO.getNombre())
                    .email(estudianteDTO.getEmail())
                    .password("passwordTemporal")
                    .rol(RolUsuario.ESTUDIANTE)
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .carrera("Por definir")
                    .semestre(1)
                    .promedioAcumulado(0.0)
                    .semaforo(SemaforoAcademico.AZUL)
                    .build();

            Estudiante estudianteCreado = estudianteService.crearEstudiante(estudiante);
            EstudianteInfoDTO responseDTO = convertirAInfoDTO(estudianteCreado);

            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<EstudianteInfoDTO>> obtenerTodosLosEstudiantes() {
        List<Estudiante> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
        List<EstudianteInfoDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirAInfoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteInfoDTO> obtenerEstudiantePorId(@PathVariable String id) {
        return estudianteService.obtenerEstudiantePorId(id)
                .map(this::convertirAInfoDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EstudianteInfoDTO> obtenerEstudiantePorCodigo(@PathVariable String codigo) {
        return estudianteService.obtenerEstudiantePorCodigo(codigo)
                .map(this::convertirAInfoDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/carrera/{carrera}")
    public ResponseEntity<List<EstudianteInfoDTO>> obtenerEstudiantesPorCarrera(@PathVariable String carrera) {
        List<Estudiante> estudiantes = estudianteService.obtenerEstudiantesPorCarrera(carrera);
        List<EstudianteInfoDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirAInfoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @GetMapping("/semestre/{semestre}")
    public ResponseEntity<List<EstudianteInfoDTO>> obtenerEstudiantesPorSemestre(@PathVariable Integer semestre) {
        List<Estudiante> estudiantes = estudianteService.obtenerEstudiantesPorSemestre(semestre);
        List<EstudianteInfoDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirAInfoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstudiante(
            @PathVariable String id,
            @Valid @RequestBody EstudianteUpdateDTO estudianteUpdateDTO) {
        try {
            Estudiante estudianteExistente = estudianteService.obtenerEstudiantePorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

            if (estudianteUpdateDTO.getNombre() != null) {
                estudianteExistente.setNombre(estudianteUpdateDTO.getNombre());
            }
            if (estudianteUpdateDTO.getEmail() != null) {
                estudianteExistente.setEmail(estudianteUpdateDTO.getEmail());
            }

            Estudiante estudianteActualizado = estudianteService.actualizarEstudiante(estudianteExistente);
            EstudianteInfoDTO responseDTO = convertirAInfoDTO(estudianteActualizado);

            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEstudiante(@PathVariable String id) {
        try {
            estudianteService.eliminarEstudiante(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private EstudianteInfoDTO convertirAInfoDTO(Estudiante estudiante) {
        EstudianteInfoDTO dto = new EstudianteInfoDTO();
        dto.setId(estudiante.getId());
        dto.setCodigo(estudiante.getCodigo());
        dto.setNombre(estudiante.getNombre());
        dto.setCarrera(estudiante.getCarrera());
        dto.setSemestre(estudiante.getSemestre());
        dto.setSemaforoAcademico(estudiante.getSemaforo() != null ?
                estudiante.getSemaforo().name() : "AZUL");
        return dto;
    }
}