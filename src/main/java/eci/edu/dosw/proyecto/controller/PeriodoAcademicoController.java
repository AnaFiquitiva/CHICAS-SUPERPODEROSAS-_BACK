package eci.edu.dosw.proyecto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import eci.edu.dosw.proyecto.model.PeriodoAcademico;
import eci.edu.dosw.proyecto.service.interfaces.PeriodoAcademicoService;
import eci.edu.dosw.proyecto.dto.PeriodoAcademicoDTO;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/periodos-academicos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PeriodoAcademicoController {

    private final PeriodoAcademicoService periodoAcademicoService;

    /**
     * Crear un nuevo período académico
     * @param periodoDTO Datos del período académico a crear
     * @return PeriodoAcademicoDTO creado
     */
    @PostMapping
    public ResponseEntity<?> crearPeriodoAcademico(@Valid @RequestBody PeriodoAcademicoDTO periodoDTO) {
        try {
            PeriodoAcademico periodo = PeriodoAcademico.builder()
                    .nombre(periodoDTO.getNombre())
                    .fechaInicio(periodoDTO.getFechaInicio())
                    .fechaFin(periodoDTO.getFechaFin())
                    .fechaInicioSolicitudes(periodoDTO.getFechaInicioSolicitudes())
                    .fechaFinSolicitudes(periodoDTO.getFechaFinSolicitudes())
                    .descripcion(periodoDTO.getDescripcion())
                    .activo(false)
                    .build();

            PeriodoAcademico periodoCreado = periodoAcademicoService.crearPeriodo(periodo);
            PeriodoAcademicoDTO responseDTO = convertirADTO(periodoCreado);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtener todos los períodos académicos
     * @return Lista de PeriodoAcademicoDTO
     */
    @GetMapping
    public ResponseEntity<List<PeriodoAcademicoDTO>> obtenerTodosLosPeriodos() {
        List<PeriodoAcademico> periodos = periodoAcademicoService.obtenerTodosLosPeriodos();
        List<PeriodoAcademicoDTO> periodosDTO = periodos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(periodosDTO);
    }

    /**
     * Obtener un período académico por ID
     * @param id ID del período académico
     * @return PeriodoAcademicoDTO encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<PeriodoAcademicoDTO> obtenerPeriodoPorId(@PathVariable String id) {
        return periodoAcademicoService.obtenerPeriodoPorId(id)
                .map(this::convertirADTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener el período académico activo actual
     * @return PeriodoAcademicoDTO activo
     */
    @GetMapping("/activo")
    public ResponseEntity<PeriodoAcademicoDTO> obtenerPeriodoActivo() {
        return periodoAcademicoService.obtenerPeriodoActivo()
                .map(this::convertirADTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Verificar si existe un período académico activo
     * @return true si existe un período activo
     */
    @GetMapping("/existe-activo")
    public ResponseEntity<Boolean> existePeriodoActivo() {
        boolean existe = periodoAcademicoService.existePeriodoActivo();
        return ResponseEntity.ok(existe);
    }

    /**
     * Obtener el período académico con solicitudes activas
     * @return PeriodoAcademicoDTO con solicitudes activas
     */
    @GetMapping("/solicitudes-activas")
    public ResponseEntity<PeriodoAcademicoDTO> obtenerPeriodoSolicitudesActivo() {
        return periodoAcademicoService.obtenerPeriodoSolicitudesActivo()
                .map(this::convertirADTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualizar un período académico existente
     * @param id ID del período académico a actualizar
     * @param periodoDTO Datos actualizados del período
     * @return PeriodoAcademicoDTO actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPeriodoAcademico(
            @PathVariable String id,
            @Valid @RequestBody PeriodoAcademicoDTO periodoDTO) {
        try {
            PeriodoAcademico periodoExistente = periodoAcademicoService.obtenerPeriodoPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Período no encontrado"));

            periodoExistente.setNombre(periodoDTO.getNombre());
            periodoExistente.setFechaInicio(periodoDTO.getFechaInicio());
            periodoExistente.setFechaFin(periodoDTO.getFechaFin());
            periodoExistente.setFechaInicioSolicitudes(periodoDTO.getFechaInicioSolicitudes());
            periodoExistente.setFechaFinSolicitudes(periodoDTO.getFechaFinSolicitudes());
            periodoExistente.setDescripcion(periodoDTO.getDescripcion());

            PeriodoAcademico periodoActualizado = periodoAcademicoService.actualizarPeriodo(periodoExistente);
            PeriodoAcademicoDTO responseDTO = convertirADTO(periodoActualizado);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Activar un período académico
     * @param id ID del período académico a activar
     * @return PeriodoAcademicoDTO activado
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<PeriodoAcademicoDTO> activarPeriodo(@PathVariable String id) {
        try {
            PeriodoAcademico periodoExistente = periodoAcademicoService.obtenerPeriodoPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Período no encontrado"));

            periodoExistente.setActivo(true);
            PeriodoAcademico periodoActualizado = periodoAcademicoService.actualizarPeriodo(periodoExistente);
            PeriodoAcademicoDTO responseDTO = convertirADTO(periodoActualizado);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar un período académico
     * @param id ID del período académico a eliminar
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPeriodoAcademico(@PathVariable String id) {
        try {
            periodoAcademicoService.eliminarPeriodo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private PeriodoAcademicoDTO convertirADTO(PeriodoAcademico periodo) {
        PeriodoAcademicoDTO dto = new PeriodoAcademicoDTO();
        dto.setNombre(periodo.getNombre());
        dto.setFechaInicio(periodo.getFechaInicio());
        dto.setFechaFin(periodo.getFechaFin());
        dto.setFechaInicioSolicitudes(periodo.getFechaInicioSolicitudes());
        dto.setFechaFinSolicitudes(periodo.getFechaFinSolicitudes());
        dto.setDescripcion(periodo.getDescripcion());
        return dto;
    }
}