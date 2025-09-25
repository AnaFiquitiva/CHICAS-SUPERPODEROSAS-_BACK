package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends MongoRepository<Notificacion, String> {
    List<Notificacion> findByDestinatarioId(String destinatarioId);
    List<Notificacion> findByTipo(TipoNotificacion tipo);
    List<Notificacion> findByLeidaFalse();

    @Query("{ 'destinatario.id': ?0, 'leida': false }")
    List<Notificacion> findNoLeidasByUsuario(String usuarioId);

    @Query("{ 'fechaEnvio': { $gte: ?0 } }")
    List<Notificacion> findRecientes(LocalDateTime fecha);

    @Query(value = "{ 'destinatario.id': ?0, 'leida': false }", count = true)
    long countNoLeidasByUsuario(String usuarioId);

    @Query("{ 'destinatario.id': ?0, 'tipo': ?1, 'leida': false }")
    List<Notificacion> findNoLeidasByUsuarioAndTipo(String usuarioId, TipoNotificacion tipo);
}
