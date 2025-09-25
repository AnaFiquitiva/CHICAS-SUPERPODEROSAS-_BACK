package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends MongoRepository<Estudiante, String> {
    Optional<Estudiante> findByCodigo(String codigo);
    List<Estudiante> findByCarrera(String carrera);
    List<Estudiante> findBySemestre(Integer semestre);
    List<Estudiante> findBySemaforo(SemaforoAcademico semaforo);

    @Query("{ 'carrera': ?0, 'semestre': ?1 }")
    List<Estudiante> findByCarreraAndSemestre(String carrera, Integer semestre);

    @Query("{ 'promedioAcumulado': { $gte: ?0 } }")
    List<Estudiante> findByPromedioGreaterThanEqual(Double promedio);

    @Query("{ 'solicitudes.estado': ?0 }")
    List<Estudiante> findWithSolicitudesByEstado(String estado);
}