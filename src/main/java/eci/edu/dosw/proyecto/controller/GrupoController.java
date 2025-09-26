// src/main/java/eci/edu/dosw/proyecto/controller/GrupoController.java
package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoRepository grupoRepository;
    private final ProfesorRepository profesorRepository;
    private final MateriaRepository materiaRepository;
    private final HorarioRepository horarioRepository;

    @GetMapping
    public List<GrupoResponseDTO> getAllGrupos() {
        return grupoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDTO> getGrupoById(@PathVariable String id) {
        return grupoRepository.findById(id)
                .map(grupo -> ResponseEntity.ok(toResponseDTO(grupo)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GrupoResponseDTO> createGrupo(@Valid @RequestBody GrupoRequestDTO request) {
        Grupo grupo = toEntity(request);
        grupo = grupoRepository.save(grupo);
        return ResponseEntity.ok(toResponseDTO(grupo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrupoResponseDTO> updateGrupo(@PathVariable String id, @Valid @RequestBody GrupoRequestDTO request) {
        return grupoRepository.findById(id)
                .map(existing -> {
                    Grupo updated = toEntity(request);
                    updated.setId(id);
                    grupoRepository.save(updated);
                    return ResponseEntity.ok(toResponseDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrupo(@PathVariable String id) {
        if (grupoRepository.existsById(id)) {
            grupoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Conversión de entidad a DTO
    private GrupoResponseDTO toResponseDTO(Grupo grupo) {
        return GrupoResponseDTO.builder()
                .id(grupo.getId())
                .codigo(grupo.getCodigo())
                .cupoMaximo(grupo.getCupoMaximo())
                .capacidadActual(grupo.getCapacidadActual())
                .disponibilidad(grupo.getDisponibilidad())
                .activo(grupo.isActivo())
                .aula(grupo.getAula())
                .edificio(grupo.getEdificio())
                .porcentajeOcupacion(grupo.getPorcentajeOcupacion())
                .estaCercaDelLimite(grupo.estaCercaDelLimite())
                .estaLleno(grupo.estaLleno())
                .profesor(grupo.getProfesor() != null ? toProfesorSummaryDTO(grupo.getProfesor()) : null)
                .materia(grupo.getMateria() != null ? toMateriaSummaryDTO(grupo.getMateria()) : null)
                .build();
    }

    // Conversión de DTO a entidad
    private Grupo toEntity(GrupoRequestDTO dto) {
        Profesor profesor = profesorRepository.findById(dto.getProfesorId())
                .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado"));
        Materia materia = materiaRepository.findById(dto.getMateriaId())
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada"));

        List<Horario> horarios = dto.getHorarios().stream()
                .map(hr -> {
                    Horario horario = Horario.builder()
                            .dia(hr.getDia())
                            .horaInicio(hr.getHoraInicio())
                            .horaFin(hr.getHoraFin())
                            .aula(hr.getAula())
                            .edificio(hr.getEdificio())
                            .build();
                    return horarioRepository.save(horario);
                })
                .collect(Collectors.toList());

        return Grupo.builder()
                .codigo(dto.getCodigo())
                .cupoMaximo(dto.getCupoMaximo())
                .activo(dto.isActivo())
                .aula(dto.getAula())
                .edificio(dto.getEdificio())
                .profesor(profesor)
                .materia(materia)
                .horarios(horarios)
                .capacidadActual(0)
                .build();
    }

    private ProfesorSummaryDTO toProfesorSummaryDTO(Profesor profesor) {
        return ProfesorSummaryDTO.builder()
                .id(profesor.getId())
                .codigo(profesor.getCodigo())
                .nombre(profesor.getNombre())
                .departamento(profesor.getDepartamento())
                .especialidad(profesor.getEspecialidad())
                .build();
    }

    private MateriaSummaryDTO toMateriaSummaryDTO(Materia materia) {
        return MateriaSummaryDTO.builder()
                .id(materia.getId())
                .codigo(materia.getCodigo())
                .nombre(materia.getNombre())
                .creditos(materia.getCreditos())
                .facultad(materia.getFacultad())
                .electiva(materia.isElectiva())
                .numeroGruposActivos(materia.getNumeroGruposActivos())
                .build();
    }
}
