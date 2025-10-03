package eci.edu.dosw.proyecto.controller;

import eci.edu.dosw.proyecto.dto.GrupoRequestDTO;
import eci.edu.dosw.proyecto.dto.GrupoInfoDTO;
import eci.edu.dosw.proyecto.model.Grupo;
import eci.edu.dosw.proyecto.service.interfaces.GrupoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GruproController {

    private final GrupoService grupoService;

    @PostMapping
    public ResponseEntity<?> crearGrupo(@Valid @RequestBody GrupoRequestDTO grupoRequestDTO) {
        try {
            Grupo grupo = Grupo.builder()
                    .codigo(grupoDTO.getCodigo())
                    .cupoMaximo(30) // Valor por defecto
                    .capacidadActual(0)
                    .activo(true)
                    .build();

            Grupo grupoCreado = grupoService.crearGrupo(grupo);
            GrupoInfoDTO responseDTO = convertirAInfoDTO(grupoCreado);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<GrupoInfoDTO>> obtenerTodosLosGrupos() {
        List<Grupo> grupos = grupoService.obtenerTodosLosGrupos();
        List<GrupoInfoDTO> gruposDTO = grupos.stream()
                .map(this::convertirAInfoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gruposDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoInfoDTO> obtenerGrupoPorId(@PathVariable String id) {
        return grupoService.obtenerGrupoPorId(id)
                .map(this::convertirAInfoDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarGrupo(
            @PathVariable String id,
            @Valid @RequestBody GrupoRequestDTO grupoRequestDTODTO) {
        try {
            Grupo grupoExistente = grupoService.obtenerGrupoPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));

            grupoExistente.setCodigo(grupoDTO.getCodigo());
            Grupo grupoActualizado = grupoService.actualizarGrupo(grupoExistente);
            GrupoInfoDTO responseDTO = convertirAInfoDTO(grupoActualizado);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGrupo(@PathVariable String id) {
        try {
            grupoService.eliminarGrupo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private GrupoInfoDTO convertirAInfoDTO(Grupo grupo) {
        GrupoInfoDTO dto = new GrupoInfoDTO();
        dto.setId(grupo.getId());
        dto.setCodigo(grupo.getCodigo());
        dto.setCupoMaximo(grupo.getCupoMaximo());
        dto.setCapacidadActual(grupo.getCapacidadActual());
        dto.setProfesor(grupo.getProfesor() != null ? grupo.getProfesor().getNombre() : "Sin asignar");
        dto.setTieneCupo(grupo.tieneCupo());
        return dto;
    }
}