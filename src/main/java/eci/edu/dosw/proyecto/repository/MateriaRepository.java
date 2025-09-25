package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends MongoRepository<Materia, String> {
    Optional<Materia> findByCodigo(String codigo);
    List<Materia> findByFacultad(String facultad);
    List<Materia> findByActivaTrue();
    List<Materia> findByElectivaTrue();

    @Query("{ 'semestreRecomendado': ?0, 'activa': true }")
    List<Materia> findBySemestreRecomendadoAndActiva(Integer semestre);

    @Query("{ 'facultad': ?0, 'electiva': ?1 }")
    List<Materia> findByFacultadAndElectiva(String facultad, boolean electiva);

    @Query("{ 'grupos.cupoMaximo': { $gt: 0 } }")
    List<Materia> findWithCuposDisponibles();
}