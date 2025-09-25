package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.util.List;

@Repository
public interface InscripcionRepository extends MongoRepository<Inscripcion, String> {
    List<Inscripcion> findByEstudianteId(String estudianteId);
    List<Inscripcion> findByGrupoId(String grupoId);
    List<Inscripcion> findByActivaTrue();

    @Query("{ 'estudiante.id': ?0, 'activa': true }")
    List<Inscripcion> findActivasByEstudiante(String estudianteId);

    @Query("{ 'grupo.id': ?0, 'activa': true }")
    List<Inscripcion> findActivasByGrupo(String grupoId);

    @Query("{ 'estudiante.id': ?0, 'grupo.materia.id': ?1 }")
    List<Inscripcion> findByEstudianteAndMateria(String estudianteId, String materiaId);

    @Query(value = "{ 'grupo.id': ?0 }", count = true)
    long countByGrupo(String grupoId);
}
