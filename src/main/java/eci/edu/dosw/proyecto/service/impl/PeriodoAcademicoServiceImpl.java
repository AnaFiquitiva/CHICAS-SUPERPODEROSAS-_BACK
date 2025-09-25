package eci.edu.dosw.proyecto.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import eci.edu.dosw.proyecto.model.*;
import eci.edu.dosw.proyecto.repository.*;
import eci.edu.dosw.proyecto.service.interfaces.PeriodoAcademicoService;
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
        List<PeriodoAcademico> periodosSolapados = periodoAcademicoRepository.findPeriodosVigentes(periodo.getFechaInicio());
        if (!periodosSolapados.isEmpty()) {
            throw new IllegalArgumentException("El período se solapa con otro período existente.");
        }

        return periodoAcademicoRepository.save(periodo);
    }

    @Override
    public Optional<PeriodoAcademico> obtenerPeriodoPorId(String id) {
        return periodoAcademicoRepository.findById(id);
    }

    @Override
    public List<PeriodoAcademico> obtenerTodosLosPeriodos() {
        return periodoAcademicoRepository.findAll();
    }

    @Override
    public PeriodoAcademico actualizarPeriodo(PeriodoAcademico periodo) {
        if (!periodo.isValid()) {
            throw new IllegalArgumentException("Periodo académico no válido: " + periodo.getValidationErrors());
        }

        // Verificar que el período exista
        Optional<PeriodoAcademico> periodoExistente = periodoAcademicoRepository.findById(periodo.getId());
        if (periodoExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Período no encontrado con id: " + periodo.getId());
        }

        // Validar que no se solapen las fechas con otros períodos (excluyendo el actual)
        List<PeriodoAcademico> periodosSolapados = periodoAcademicoRepository.findPeriodosVigentes(periodo.getFechaInicio());
        periodosSolapados.removeIf(p -> p.getId().equals(periodo.getId()));
        if (!periodosSolapados.isEmpty()) {
            throw new IllegalArgumentException("El período se solapa con otro período existente.");
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
}