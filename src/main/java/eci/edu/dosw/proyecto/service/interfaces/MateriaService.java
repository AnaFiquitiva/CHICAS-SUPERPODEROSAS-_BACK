package eci.edu.dosw.proyecto.service.interfaces;

import eci.edu.dosw.proyecto.model.Materia;
import java.util.List;
import java.util.Optional;

public interface MateriaService {
    Materia crearMateria(Materia materia);
    Optional<Materia> obtenerMateriaPorId(String id);
    Optional<Materia> obtenerMateriaPorCodigo(String codigo);
    List<Materia> obtenerTodasLasMaterias();
    List<Materia> obtenerMateriasActivas();
    List<Materia> obtenerMateriasPorFacultad(String facultad);
    List<Materia> obtenerMateriasPorSemestre(Integer semestre);
    List<Materia> obtenerMateriasConCuposDisponibles();
    List<Materia> obtenerMateriasElectivas();
    Materia actualizarMateria(Materia materia);
    void eliminarMateria(String id);
    Materia desactivarMateria(String id);
    Materia activarMateria(String id);
    List<Materia> buscarMateriasPorNombre(String nombre);
}