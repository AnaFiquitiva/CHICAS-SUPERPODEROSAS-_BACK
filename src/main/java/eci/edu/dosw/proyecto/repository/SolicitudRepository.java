package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitudRepository extends MongoRepository<Solicitud, String> {
    List<Solicitud> findByEstado(EstadoSolicitud estado);
    List<Solicitud> findByEstudianteId(String estudianteId);
    List<Solicitud> findByDecanoAsignadoId(String decanoId);

    @Query("{ 'materiaDestino.facultad': ?0 }")
    List<Solicitud> findByFacultadDestino(String facultad);

    @Query("{ 'estado': { $in: ['PENDIENTE', 'EN_REVISION'] } }")
    List<Solicitud> findSolicitudesPendientes();

    @Query("{ 'fechaCreacion': { $gte: ?0, $lte: ?1 } }")
    List<Solicitud> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("{ 'estado': ?0, 'fechaCreacion': { $lt: ?1 } }")
    List<Solicitud> findVencidasByEstado(EstadoSolicitud estado, LocalDateTime fechaLimite);

    @Query(value = "{ 'materiaDestino.id': ?0 }", count = true)
    long countByMateriaDestino(String materiaId);

    @Query("{ 'estudiante.id': ?0, 'estado': { $in: ['PENDIENTE', 'EN_REVISION'] } }")
    List<Solicitud> findSolicitudesActivasByEstudiante(String estudianteId);
}
