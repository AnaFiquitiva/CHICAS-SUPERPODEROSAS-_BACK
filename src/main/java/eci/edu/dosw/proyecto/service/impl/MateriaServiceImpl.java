package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.Materia;
import eci.edu.dosw.proyecto.repository.MateriaRepository;
import eci.edu.dosw.proyecto.service.interfaces.MateriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MateriaServiceImpl implements MateriaService {

    private final MateriaRepository materiaRepository;

    @Override
    public Materia crearMateria(Materia materia) {
        if (!materia.isValid()) {
            throw new IllegalArgumentException("Materia no válida: " + materia.getValidationErrors());
        }

        // Validar que no exista otra materia con el mismo código
        Optional<Materia> materiaExistente = materiaRepository.findByCodigo(materia.getCodigo());
        if (materiaExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe una materia con el código: " + materia.getCodigo());
        }

        // Valores por defecto
        if (materia.getSemestreRecomendado() == null) {
            materia.setSemestreRecomendado(1);
        }
        materia.setActiva(true);

        return materiaRepository.save(materia);
    }

    @Override
    public Optional<Materia> obtenerMateriaPorId(String id) {
        return materiaRepository.findById(id);
    }

    @Override
    public Optional<Materia> obtenerMateriaPorCodigo(String codigo) {
        return materiaRepository.findByCodigo(codigo);
    }

    @Override
    public List<Materia> obtenerTodasLasMaterias() {
        return materiaRepository.findAll();
    }

    @Override
    public List<Materia> obtenerMateriasActivas() {
        return materiaRepository.findByActivaTrue();
    }

    @Override
    public List<Materia> obtenerMateriasPorFacultad(String facultad) {
        return materiaRepository.findByFacultad(facultad);
    }

    @Override
    public List<Materia> obtenerMateriasPorSemestre(Integer semestre) {
        return materiaRepository.findBySemestreRecomendadoAndActiva(semestre);
    }

    @Override
    public List<Materia> obtenerMateriasConCuposDisponibles() {
        return materiaRepository.findWithCuposDisponibles();
    }

    @Override
    public List<Materia> obtenerMateriasElectivas() {
        return materiaRepository.findByElectivaTrue();
    }

    @Override
    public Materia actualizarMateria(Materia materia) {
        if (!materia.isValid()) {
            throw new IllegalArgumentException("Materia no válida: " + materia.getValidationErrors());
        }

        Optional<Materia> materiaExistente = materiaRepository.findById(materia.getId());
        if (materiaExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Materia no encontrada con id: " + materia.getId());
        }

        // Validar que el código no esté duplicado (excluyendo la materia actual)
        Optional<Materia> materiaConMismoCodigo = materiaRepository.findByCodigo(materia.getCodigo());
        if (materiaConMismoCodigo.isPresent() && !materiaConMismoCodigo.get().getId().equals(materia.getId())) {
            throw new IllegalArgumentException("Ya existe otra materia con el código: " + materia.getCodigo());
        }

        return materiaRepository.save(materia);
    }

    @Override
    public void eliminarMateria(String id) {
        if (!materiaRepository.existsById(id)) {
            throw new IllegalArgumentException("Materia no encontrada con id: " + id);
        }
        materiaRepository.deleteById(id);
    }

    @Override
    public Materia desactivarMateria(String id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada con id: " + id));
        materia.setActiva(false);
        return materiaRepository.save(materia);
    }

    @Override
    public Materia activarMateria(String id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada con id: " + id));
        materia.setActiva(true);
        return materiaRepository.save(materia);
    }

    @Override
    public List<Materia> buscarMateriasPorNombre(String nombre) {
        // Búsqueda case-insensitive por nombre
        List<Materia> todasMaterias = materiaRepository.findAll();
        return todasMaterias.stream()
                .filter(m -> m.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}