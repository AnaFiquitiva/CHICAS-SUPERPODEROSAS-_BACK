package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.Decano;
import eci.edu.dosw.proyecto.model.Materia;
import eci.edu.dosw.proyecto.repository.DecanoRepository;
import eci.edu.dosw.proyecto.repository.MateriaRepository;
import eci.edu.dosw.proyecto.service.interfaces.DecanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DecanoServiceImpl implements DecanoService {

    private final DecanoRepository decanoRepository;
    private final MateriaRepository materiaRepository;

    @Override
    public Decano crearDecano(Decano decano) {
        if (!decano.isValid()) {
            throw new IllegalArgumentException("Decano no válido: " + decano.getValidationErrors());
        }

        // Validar que no exista otro decano con el mismo código
        Optional<Decano> decanoExistente = decanoRepository.findById(decano.getId());
        if (decanoExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un decano con el código: " + decano.getCodigo());
        }

        // Validar que no exista otro decano para la misma facultad
        Optional<Decano> decanoMismaFacultad = decanoRepository.findByFacultad(decano.getFacultad());
        if (decanoMismaFacultad.isPresent()) {
            throw new IllegalArgumentException("Ya existe un decano para la facultad: " + decano.getFacultad());
        }

        return decanoRepository.save(decano);
    }

    @Override
    public Optional<Decano> obtenerDecanoPorId(String id) {
        return decanoRepository.findById(id);
    }

    @Override
    public Optional<Decano> obtenerDecanoPorCodigo(String codigo) {
        // Necesitamos buscar en todos los decanos por código
        return decanoRepository.findAll().stream()
                .filter(d -> d.getCodigo().equals(codigo))
                .findFirst();
    }

    @Override
    public Optional<Decano> obtenerDecanoPorFacultad(String facultad) {
        return decanoRepository.findByFacultad(facultad);
    }

    @Override
    public List<Decano> obtenerTodosLosDecanos() {
        return decanoRepository.findAll();
    }

    @Override
    public List<Decano> obtenerDecanosPorDepartamento(String departamento) {
        return decanoRepository.findByDepartamento(departamento);
    }

    @Override
    public List<Decano> obtenerDecanosConSolicitudesPendientes() {
        return decanoRepository.findWithSolicitudesPendientes();
    }

    @Override
    public Decano actualizarDecano(Decano decano) {
        if (!decano.isValid()) {
            throw new IllegalArgumentException("Decano no válido: " + decano.getValidationErrors());
        }

        Optional<Decano> decanoExistente = decanoRepository.findById(decano.getId());
        if (decanoExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Decano no encontrado con id: " + decano.getId());
        }

        // Validar que la facultad no esté duplicada (excluyendo el decano actual)
        Optional<Decano> decanoMismaFacultad = decanoRepository.findByFacultad(decano.getFacultad());
        if (decanoMismaFacultad.isPresent() && !decanoMismaFacultad.get().getId().equals(decano.getId())) {
            throw new IllegalArgumentException("Ya existe otro decano para la facultad: " + decano.getFacultad());
        }

        return decanoRepository.save(decano);
    }

    @Override
    public void eliminarDecano(String id) {
        if (!decanoRepository.existsById(id)) {
            throw new IllegalArgumentException("Decano no encontrado con id: " + id);
        }
        decanoRepository.deleteById(id);
    }

    @Override
    public Decano asignarMateriaGestionada(String decanoId, String materiaId) {
        Decano decano = decanoRepository.findById(decanoId)
                .orElseThrow(() -> new IllegalArgumentException("Decano no encontrado con id: " + decanoId));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada con id: " + materiaId));

        // Verificar que la materia pertenezca a la facultad del decano
        if (!materia.getFacultad().equals(decano.getFacultad())) {
            throw new IllegalArgumentException("La materia no pertenece a la facultad del decano");
        }

        // Verificar que no esté ya asignada
        if (decano.getMateriasGestionadas().stream()
                .anyMatch(m -> m.getId().equals(materiaId))) {
            throw new IllegalArgumentException("La materia ya está asignada al decano");
        }

        decano.getMateriasGestionadas().add(materia);
        return decanoRepository.save(decano);
    }

    @Override
    public Decano removerMateriaGestionada(String decanoId, String materiaId) {
        Decano decano = decanoRepository.findById(decanoId)
                .orElseThrow(() -> new IllegalArgumentException("Decano no encontrado con id: " + decanoId));

        decano.getMateriasGestionadas().removeIf(m -> m.getId().equals(materiaId));
        return decanoRepository.save(decano);
    }
}