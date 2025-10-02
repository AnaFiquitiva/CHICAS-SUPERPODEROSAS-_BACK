package eci.edu.dosw.proyecto.service.impl;

import eci.edu.dosw.proyecto.model.Estudiante;
import eci.edu.dosw.proyecto.model.SemaforoAcademico;
import eci.edu.dosw.proyecto.repository.EstudianteRepository;
import eci.edu.dosw.proyecto.service.interfaces.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final EstudianteRepository estudianteRepository;

    @Override
    public Estudiante crearEstudiante(Estudiante estudiante) {
        if (!estudiante.isValid()) {
            throw new IllegalArgumentException("Estudiante no válido: " + estudiante.getValidationErrors());
        }

        Optional<Estudiante> estudianteExistente = estudianteRepository.findByCodigo(estudiante.getCodigo());
        if (estudianteExistente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un estudiante con el código: " + estudiante.getCodigo());
        }

        return estudianteRepository.save(estudiante);
    }

    @Override
    public Optional<Estudiante> obtenerEstudiantePorId(String id) {
        return estudianteRepository.findById(id);
    }

    @Override
    public Optional<Estudiante> obtenerEstudiantePorCodigo(String codigo) {
        return estudianteRepository.findByCodigo(codigo);
    }

    @Override
    public List<Estudiante> obtenerEstudiantesPorCarrera(String carrera) {
        return estudianteRepository.findByCarrera(carrera);
    }

    @Override
    public List<Estudiante> obtenerEstudiantesPorSemestre(Integer semestre) {
        return estudianteRepository.findBySemestre(semestre);
    }

    @Override
    public List<Estudiante> obtenerEstudiantesPorSemaforo(SemaforoAcademico semaforo) {
        return estudianteRepository.findBySemaforo(semaforo);
    }

    @Override
    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteRepository.findAll();
    }

    @Override
    public Estudiante actualizarEstudiante(Estudiante estudiante) {
        if (!estudiante.isValid()) {
            throw new IllegalArgumentException("Estudiante no válido: " + estudiante.getValidationErrors());
        }

        Optional<Estudiante> estudianteExistente = estudianteRepository.findById(estudiante.getId());
        if (estudianteExistente.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. Estudiante no encontrado con id: " + estudiante.getId());
        }

        Optional<Estudiante> estudianteConMismoCodigo = estudianteRepository.findByCodigo(estudiante.getCodigo());
        if (estudianteConMismoCodigo.isPresent() && !estudianteConMismoCodigo.get().getId().equals(estudiante.getId())) {
            throw new IllegalArgumentException("Ya existe otro estudiante con el código: " + estudiante.getCodigo());
        }

        return estudianteRepository.save(estudiante);
    }

    @Override
    public void eliminarEstudiante(String id) {
        if (!estudianteRepository.existsById(id)) {
            throw new IllegalArgumentException("Estudiante no encontrado con id: " + id);
        }
        estudianteRepository.deleteById(id);
    }

    @Override
    public Estudiante actualizarSemaforo(String estudianteId, double promedio) {
        Estudiante estudiante = obtenerEstudiantePorId(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado con id: " + estudianteId));

        estudiante.actualizarSemaforo(promedio);
        return estudianteRepository.save(estudiante);
    }
}