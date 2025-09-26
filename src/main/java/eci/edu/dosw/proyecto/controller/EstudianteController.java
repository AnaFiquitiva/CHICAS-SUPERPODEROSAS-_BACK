package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import eci.edu.dosw.proyecto.model.Estudiante;
import eci.edu.dosw.proyecto.model.SemaforoAcademico;
import eci.edu.dosw.proyecto.service.interfaces.EstudianteService;
import eci.edu.dosw.proyecto.dto.EstudianteRequestDTO;
import eci.edu.dosw.proyecto.dto.EstudianteResponseDTO;
import eci.edu.dosw.proyecto.dto.EstudianteSummaryDTO;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class EstudianteController {

    private final EstudianteService estudianteService;

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crearEstudiante(@Valid @RequestBody EstudianteRequestDTO estudianteDTO) {
        try {
            Estudiante estudiante = convertirAEntidad(estudianteDTO);

            Estudiante estudianteCreado = estudianteService.crearEstudiante(estudiante);
            EstudianteResponseDTO responseDTO = convertirAResponseDTO(estudianteCreado);

            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> obtenerEstudiantePorId(@PathVariable String id) {
        return estudianteService.obtenerEstudiantePorId(id)
                .map(this::convertirAResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EstudianteResponseDTO> obtenerEstudiantePorCodigo(@PathVariable String codigo) {
        return estudianteService.obtenerEstudiantePorCodigo(codigo)
                .map(this::convertirAResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EstudianteSummaryDTO>> obtenerTodosLosEstudiantes() {
        List<Estudiante> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
        List<EstudianteSummaryDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirASummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @GetMapping("/carrera/{carrera}")
    public ResponseEntity<List<EstudianteSummaryDTO>> obtenerEstudiantesPorCarrera(@PathVariable String carrera) {
        List<Estudiante> estudiantes = estudianteService.obtenerEstudiantesPorCarrera(carrera);
        List<EstudianteSummaryDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirASummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @GetMapping("/semestre/{semestre}")
    public ResponseEntity<List<EstudianteSummaryDTO>> obtenerEstudiantesPorSemestre(@PathVariable Integer semestre) {
        List<Estudiante> estudiantes = estudianteService.obtenerEstudiantesPorSemestre(semestre);
        List<EstudianteSummaryDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirASummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @GetMapping("/semaforo/{semaforo}")
    public ResponseEntity<List<EstudianteSummaryDTO>> obtenerEstudiantesPorSemaforo(@PathVariable SemaforoAcademico semaforo) {
        List<Estudiante> estudiantes = estudianteService.obtenerEstudiantesPorSemaforo(semaforo);
        List<EstudianteSummaryDTO> estudiantesDTO = estudiantes.stream()
                .map(this::convertirASummaryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estudiantesDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizarEstudiante(
            @PathVariable String id,
            @Valid @RequestBody EstudianteRequestDTO estudianteDTO) {
        try {
            Estudiante estudiante = convertirAEntidad(estudianteDTO);
            estudiante.setId(id);

            Estudiante estudianteActualizado = estudianteService.actualizarEstudiante(estudiante);
            EstudianteResponseDTO responseDTO = convertirAResponseDTO(estudianteActualizado);

            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/semaforo")
    public ResponseEntity<EstudianteResponseDTO> actualizarSemaforo(
            @PathVariable String id,
            @RequestParam Double promedio) {
        try {
            Estudiante estudianteActualizado = estudianteService.actualizarSemaforo(id, promedio);
            EstudianteResponseDTO responseDTO = convertirAResponseDTO(estudianteActualizado);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
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

   
    private Estudiante convertirAEntidad(EstudianteRequestDTO dto) {
        return Estudiante.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .rol(dto.getRol())
                .activo(dto.isActivo())
                .carrera(dto.getCarrera())
                .semestre(dto.getSemestre())
                .promedioAcumulado(dto.getPromedioAcumulado())
                .build();
    }

    private EstudianteResponseDTO convertirAResponseDTO(Estudiante estudiante) {
        return EstudianteResponseDTO.builder()
                .id(estudiante.getId())
                .codigo(estudiante.getCodigo())
                .nombre(estudiante.getNombre())
                .email(estudiante.getEmail())
                .rol(estudiante.getRol())
                .activo(estudiante.isActivo())
                .fechaCreacion(estudiante.getFechaCreacion())
                .fechaUltimoAcceso(estudiante.getFechaUltimoAcceso())
                .carrera(estudiante.getCarrera())
                .semestre(estudiante.getSemestre())
                .semaforo(estudiante.getSemaforo())
                .promedioAcumulado(estudiante.getPromedioAcumulado())
                .numeroInscripciones(estudiante.getInscripciones() != null ? estudiante.getInscripciones().size() : 0)
                .numeroSolicitudes(estudiante.getSolicitudes() != null ? estudiante.getSolicitudes().size() : 0)
                .puedeSolicitarCambio(estudiante.puedeSolicitarCambio())
                .build();
    }

    private EstudianteSummaryDTO convertirASummaryDTO(Estudiante estudiante) {
        return EstudianteSummaryDTO.builder()
                .id(estudiante.getId())
                .codigo(estudiante.getCodigo())
                .nombre(estudiante.getNombre())
                .carrera(estudiante.getCarrera())
                .semestre(estudiante.getSemestre())
                .semaforo(estudiante.getSemaforo())
                .build();
    }
}
