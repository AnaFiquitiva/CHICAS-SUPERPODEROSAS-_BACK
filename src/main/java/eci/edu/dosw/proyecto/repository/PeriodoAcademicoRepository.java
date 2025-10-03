package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.PeriodoAcademico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodoAcademicoRepository extends MongoRepository<PeriodoAcademico, String> {

    Optional<PeriodoAcademico> findByActivoTrue();

    List<PeriodoAcademico> findByActivo(boolean activo);

    @Query("{ 'fechaInicioSolicitudes': { $lte: ?0 }, 'fechaFinSolicitudes': { $gte: ?0 } }")
    Optional<PeriodoAcademico> findPeriodoSolicitudesActivo(LocalDateTime fecha);

    @Query("{ $or: [ " +
            "{ 'fechaInicio': { $lte: ?0 }, 'fechaFin': { $gte: ?0 } }, " +
            "{ 'fechaInicio': { $lte: ?1 }, 'fechaFin': { $gte: ?1 } }, " +
            "{ $and: [ { 'fechaInicio': { $gte: ?0 } }, { 'fechaFin': { $lte: ?1 } } ] } " +
            "] }")
    List<PeriodoAcademico> findPeriodosSolapados(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    @Query("{ 'fechaInicio': { $lte: ?0 }, 'fechaFin': { $gte: ?0 } }")
    List<PeriodoAcademico> findPeriodosVigentes(LocalDateTime fecha);

    List<PeriodoAcademico> findByOrderByFechaInicioDesc();
}