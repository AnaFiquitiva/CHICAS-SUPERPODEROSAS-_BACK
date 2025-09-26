package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.Grupo;
import eci.edu.dosw.proyecto.repository.GrupoRepository;
import eci.edu.dosw.proyecto.service.interfaces.GrupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrupoServiceImpl implements GrupoService {

    private final GrupoRepository grupoRepository;

    @Override
    public Grupo crearGrupo(Grupo grupo) {
        if (!grupo.isValid()) {
            throw new IllegalArgumentException("Grupo no válido: " + grupo.getValidationErrors());
        }
        return grupoRepository.save(grupo);
    }

    @Override
    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoRepository.findAll();
    }

    @Override
    public Optional<Grupo> obtenerGrupoPorId(String id) {
        return grupoRepository.findById(id);
    }

    @Override
    public Grupo actualizarGrupo(Grupo grupo) {
        if (!grupo.isValid()) {
            throw new IllegalArgumentException("Grupo no válido: " + grupo.getValidationErrors());
        }
        if (!grupoRepository.existsById(grupo.getId())) {
            throw new IllegalArgumentException("Grupo no encontrado con id: " + grupo.getId());
        }
        return grupoRepository.save(grupo);
    }

    @Override
    public void eliminarGrupo(String id) {
        if (!grupoRepository.existsById(id)) {
            throw new IllegalArgumentException("Grupo no encontrado con id: " + id);
        }
        grupoRepository.deleteById(id);
    }
}