package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.Grupo;

import java.util.List;
import java.util.Optional;

public interface GrupoService {
    Grupo crearGrupo(Grupo grupo);
    List<Grupo> obtenerTodosLosGrupos();
    Optional<Grupo> obtenerGrupoPorId(String id);
    Grupo actualizarGrupo(Grupo grupo);
    void eliminarGrupo(String id);
}