package eci.edu.dosw.proyecto.service.interfaces;


import eci.edu.dosw.proyecto.model.*;
import java.util.List;
import java.util.Optional;

public interface PeriodoAcademicoService {
    PeriodoAcademico crearPeriodo(PeriodoAcademico periodo);
    Optional<PeriodoAcademico> obtenerPeriodoPorId(String id);
    List<PeriodoAcademico> obtenerTodosLosPeriodos();
    PeriodoAcademico actualizarPeriodo(PeriodoAcademico periodo);
    void eliminarPeriodo(String id);
    Optional<PeriodoAcademico> obtenerPeriodoActivo();
    boolean existePeriodoActivo();
    Optional<PeriodoAcademico> obtenerPeriodoSolicitudesActivo();
}