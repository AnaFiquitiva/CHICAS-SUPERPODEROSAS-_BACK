package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.Decano;
import java.util.List;
import java.util.Optional;

public interface DecanoService {
    Decano crearDecano(Decano decano);
    Optional<Decano> obtenerDecanoPorId(String id);
    Optional<Decano> obtenerDecanoPorCodigo(String codigo);
    Optional<Decano> obtenerDecanoPorFacultad(String facultad);
    List<Decano> obtenerTodosLosDecanos();
    List<Decano> obtenerDecanosPorDepartamento(String departamento);
    List<Decano> obtenerDecanosConSolicitudesPendientes();
    Decano actualizarDecano(Decano decano);
    void eliminarDecano(String id);
    Decano asignarMateriaGestionada(String decanoId, String materiaId);
    Decano removerMateriaGestionada(String decanoId, String materiaId);
}