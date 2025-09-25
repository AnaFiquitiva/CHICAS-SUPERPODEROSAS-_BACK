package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends MongoRepository<Grupo, String> {
    Optional<Grupo> findByCodigo(String codigo);
    List<Grupo> findByMateriaId(String materiaId);
    List<Grupo> findByActivoTrue();
    List<Grupo> findByProfesorId(String profesorId);

    @Query("{ 'materia.facultad': ?0, 'activo': true }")
    List<Grupo> findByFacultad(String facultad);

    @Query("{ 'capacidadActual': { $lt: '$cupoMaximo' }, 'activo': true }")
    List<Grupo> findWithCuposDisponibles();

    @Query("{ 'capacidadActual': { $gte: { $multiply: ['$cupoMaximo', 0.9] } }, 'activo': true }")
    List<Grupo> findCercaDelLimite();

    @Query("{ 'materia.id': ?0, 'profesor.id': ?1 }")
    List<Grupo> findByMateriaAndProfesor(String materiaId, String profesorId);
}