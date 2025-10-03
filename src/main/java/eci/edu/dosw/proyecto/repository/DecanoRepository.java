package eci.edu.dosw.proyecto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.Decano;
import java.util.List;
import java.util.Optional;

@Repository
public interface DecanoRepository extends MongoRepository<Decano, String> {
    Optional<Decano> findByFacultad(String facultad);
    List<Decano> findByDepartamento(String departamento);

    @Query("{ 'solicitudesAsignadas.estado': { $in: ['PENDIENTE', 'EN_REVISION'] } }")
    List<Decano> findWithSolicitudesPendientes();

    // Nuevos métodos
    @Query("{ 'codigo': ?0 }")
    Optional<Decano> findByCodigo(String codigo);

    @Query("{ 'facultad': { $regex: ?0, $options: 'i' } }")
    List<Decano> findByFacultadContainingIgnoreCase(String facultad);

    @Query("{ 'materiasGestionadas.id': ?0 }")
    List<Decano> findByMateriaGestionada(String materiaId);
}