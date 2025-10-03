package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.Horario;
import eci.edu.dosw.proyecto.model.DiaSemana;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HorarioService {
    Horario crearHorario(Horario horario);
    Optional<Horario> obtenerHorarioPorId(String id);
    List<Horario> obtenerTodosLosHorarios();
    List<Horario> obtenerHorariosPorDia(DiaSemana dia);
    List<Horario> obtenerHorariosPorAula(String aula);
    List<Horario> obtenerHorariosPorEdificio(String edificio);
    Horario actualizarHorario(Horario horario);
    void eliminarHorario(String id);
    boolean verificarDisponibilidadAula(String aula, DiaSemana dia, LocalTime inicio, LocalTime fin);
    List<Horario> verificarCrucesHorario(DiaSemana dia, LocalTime inicio, LocalTime fin);
    List<String> obtenerAulasDisponibles(DiaSemana dia, LocalTime inicio, LocalTime fin);
}