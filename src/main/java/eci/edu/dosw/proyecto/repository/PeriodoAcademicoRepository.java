package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoAcademicoRepository extends MongoRepository<PeriodoAcademico, String> {
    Optional<PeriodoAcademico> findByActivoTrue();
    List<PeriodoAcademico> findByNombreContainingIgnoreCase(String nombre);

    @Query("{ 'fechaInicioSolicitudes': { $lte: ?0 }, 'fechaFinSolicitudes': { $gte: ?0 } }")
    Optional<PeriodoAcademico> findPeriodoSolicitudesActivo(java.time.LocalDateTime fecha);

    @Query("{ 'activo': true, 'fechaFin': { $gte: ?0 } }")
    List<PeriodoAcademico> findPeriodosVigentes(java.time.LocalDateTime fecha);
}