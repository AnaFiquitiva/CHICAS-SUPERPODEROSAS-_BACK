package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.Horario;
import eci.edu.dosw.proyecto.model.DiaSemana;
import eci.edu.dosw.proyecto.repository.HorarioRepository;
import eci.edu.dosw.proyecto.service.interfaces.HorarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository horarioRepository;

    @Override
    public Horario crearHorario(Horario horario) {
        if (!horario.isValid()) {
            throw new IllegalArgumentException("Horario no válido: " + horario.getValidationErrors());
        }

        // Validar que no haya cruces en el mismo aula
        if (!verificarDisponibilidadAula(horario.getAula(), horario.getDia(), horario.getHoraInicio(), horario.getHoraFin())) {
            throw new IllegalArgumentException("El aula " + horario.getAula() + " ya está ocupada en ese horario");
        }

        return horarioRepository.save(horario);
    }

    @Override
    public Optional<Horario> obtenerHorarioPorId(String id) {
        return horarioRepository.findById(id);
    }

    @Override
    public List<Horario> obtenerTodosLosHorarios() {
        return horarioRepository.findAll();
    }

    @Override
    public List<Horario> obtenerHorariosPorDia(DiaSemana dia) {
        return horarioRepository.findByDia(dia);
    }

    @Override
    public List<Horario> obtenerHorariosPorAula(String aula) {
        return horarioRepository.findAll().stream()
                .filter(h -> h.getAula() != null && h.getAula().equalsIgnoreCase(aula))
                .collect(Collectors.toList());
    }

    @Override
    public List<Horario> obtenerHorariosPorEdificio(String edificio) {
        return horarioRepository.findAll().stream()
                .filter(h -> h.getEdificio() != null && h.getEdificio().equalsIgnoreCase(edificio))
                .collect(Collectors.toList());
    }

    @Override
    public Horario actualizarHorario(Horario horario) {
        if (!horario.isValid()) {
            throw new IllegalArgumentException("Horario no válido: " + horario.getValidationErrors());
        }

        Optional<Horario> horarioExistente = horarioRepository.findById(horario.getId());
        if (horarioExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Horario no encontrado con id: " + horario.getId());
        }

        // Validar disponibilidad excluyendo el horario actual
        List<Horario> horariosOcupados = horarioRepository.findAulaOcupada(
                horario.getAula(), horario.getDia(), horario.getHoraInicio(), horario.getHoraFin());

        boolean hayCruceConOtros = horariosOcupados.stream()
                .anyMatch(h -> !h.getId().equals(horario.getId()));

        if (hayCruceConOtros) {
            throw new IllegalArgumentException("El aula " + horario.getAula() + " ya está ocupada en ese horario");
        }

        return horarioRepository.save(horario);
    }

    @Override
    public void eliminarHorario(String id) {
        if (!horarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Horario no encontrado con id: " + id);
        }
        horarioRepository.deleteById(id);
    }

    @Override
    public boolean verificarDisponibilidadAula(String aula, DiaSemana dia, LocalTime inicio, LocalTime fin) {
        List<Horario> horariosOcupados = horarioRepository.findAulaOcupada(aula, dia, inicio, fin);
        return horariosOcupados.isEmpty();
    }

    @Override
    public List<Horario> verificarCrucesHorario(DiaSemana dia, LocalTime inicio, LocalTime fin) {
        return horarioRepository.findConCruce(dia, inicio, fin);
    }

    @Override
    public List<String> obtenerAulasDisponibles(DiaSemana dia, LocalTime inicio, LocalTime fin) {
        // Obtener todas las aulas únicas del sistema
        List<String> todasLasAulas = horarioRepository.findAll().stream()
                .map(Horario::getAula)
                .distinct()
                .collect(Collectors.toList());

        // Filtrar aulas disponibles
        return todasLasAulas.stream()
                .filter(aula -> verificarDisponibilidadAula(aula, dia, inicio, fin))
                .collect(Collectors.toList());
    }
}