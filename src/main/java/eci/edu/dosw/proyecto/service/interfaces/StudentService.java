package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.Estudiante;
import eci.edu.dosw.proyecto.model.SemaforoAcademico;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Estudiante crearEstudiante(Estudiante estudiante);
    Optional<Estudiante> obtenerEstudiantePorId(String id);
    Optional<Estudiante> obtenerEstudiantePorCodigo(String codigo);
    List<Estudiante> obtenerEstudiantesPorCarrera(String carrera);
    List<Estudiante> obtenerEstudiantesPorSemestre(Integer semestre);
    List<Estudiante> obtenerEstudiantesPorSemaforo(SemaforoAcademico semaforo);
    List<Estudiante> obtenerTodosLosEstudiantes();
    Estudiante actualizarEstudiante(Estudiante estudiante);
    void eliminarEstudiante(String id);
    Estudiante actualizarSemaforo(String estudianteId, double promedio);
}