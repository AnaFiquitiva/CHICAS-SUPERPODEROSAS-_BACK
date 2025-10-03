package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.PeriodoAcademico;
import eci.edu.dosw.proyecto.repository.PeriodoAcademicoRepository;
import eci.edu.dosw.proyecto.service.interfaces.PeriodoAcademicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeriodoAcademicoServiceImpl implements PeriodoAcademicoService {

    private final PeriodoAcademicoRepository periodoAcademicoRepository;

    @Override
    public PeriodoAcademico crearPeriodo(PeriodoAcademico periodo) {
        if (!periodo.isValid()) {
            throw new IllegalArgumentException("Periodo académico no válido: " + periodo.getValidationErrors());
        }

        // Validar que no se solapen las fechas con otros períodos
        List<PeriodoAcademico> periodosSolapados = periodoAcademicoRepository
                .findPeriodosSolapados(periodo.getFechaInicio(), periodo.getFechaFin());
        if (!periodosSolapados.isEmpty()) {
            throw new IllegalArgumentException("El período se solapa con otro período existente.");
        }

        // Si se marca como activo, desactivar otros períodos
        if (periodo.isActivo()) {
            desactivarOtrosPeriodos();
        }

        return periodoAcademicoRepository.save(periodo);
    }

    @Override
    public Optional<PeriodoAcademico> obtenerPeriodoPorId(String id) {
        return periodoAcademicoRepository.findById(id);
    }

    @Override
    public List<PeriodoAcademico> obtenerTodosLosPeriodos() {
        return periodoAcademicoRepository.findByOrderByFechaInicioDesc();
    }

    @Override
    public PeriodoAcademico actualizarPeriodo(PeriodoAcademico periodo) {
        if (!periodo.isValid()) {
            throw new IllegalArgumentException("Periodo académico no válido: " + periodo.getValidationErrors());
        }

        Optional<PeriodoAcademico> periodoExistente = periodoAcademicoRepository.findById(periodo.getId());
        if (periodoExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Período no encontrado con id: " + periodo.getId());
        }

        // Validar que no se solapen las fechas con otros períodos (excluyendo el actual)
        List<PeriodoAcademico> periodosSolapados = periodoAcademicoRepository
                .findPeriodosSolapados(periodo.getFechaInicio(), periodo.getFechaFin());
        periodosSolapados.removeIf(p -> p.getId().equals(periodo.getId()));
        if (!periodosSolapados.isEmpty()) {
            throw new IllegalArgumentException("El período se solapa con otro período existente.");
        }

        // Si se marca como activo, desactivar otros períodos
        if (periodo.isActivo()) {
            desactivarOtrosPeriodos();
        }

        return periodoAcademicoRepository.save(periodo);
    }

    @Override
    public void eliminarPeriodo(String id) {
        if (!periodoAcademicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Período no encontrado con id: " + id);
        }
        periodoAcademicoRepository.deleteById(id);
    }

    @Override
    public Optional<PeriodoAcademico> obtenerPeriodoActivo() {
        return periodoAcademicoRepository.findByActivoTrue();
    }

    @Override
    public boolean existePeriodoActivo() {
        return periodoAcademicoRepository.findByActivoTrue().isPresent();
    }

    @Override
    public Optional<PeriodoAcademico> obtenerPeriodoSolicitudesActivo() {
        return periodoAcademicoRepository.findPeriodoSolicitudesActivo(LocalDateTime.now());
    }

    @Override
    public List<PeriodoAcademico> obtenerPeriodosPorEstado(String estado) {
        LocalDateTime ahora = LocalDateTime.now();

        return switch (estado.toUpperCase()) {
            case "ACTIVO" -> periodoAcademicoRepository.findAll().stream()
                    .filter(PeriodoAcademico::estaActivo)
                    .toList();
            case "FUTURO" -> periodoAcademicoRepository.findAll().stream()
                    .filter(p -> p.getFechaInicio().isAfter(ahora))
                    .toList();
            case "PASADO" -> periodoAcademicoRepository.findAll().stream()
                    .filter(p -> p.getFechaFin().isBefore(ahora))
                    .toList();
            case "SOLICITUDES_ACTIVO" -> periodoAcademicoRepository.findAll().stream()
                    .filter(PeriodoAcademico::periodoSolicitudesActivo)
                    .toList();
            default -> periodoAcademicoRepository.findAll();
        };
    }

    @Override
    public PeriodoAcademico activarPeriodo(String id) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Período no encontrado con id: " + id));

        desactivarOtrosPeriodos();
        periodo.setActivo(true);

        return periodoAcademicoRepository.save(periodo);
    }

    private void desactivarOtrosPeriodos() {
        List<PeriodoAcademico> periodosActivos = periodoAcademicoRepository.findByActivo(true);
        periodosActivos.forEach(p -> p.setActivo(false));
        periodoAcademicoRepository.saveAll(periodosActivos);
    }
}