package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.AuditLog;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByPerformedBy(User user);

    List<AuditLog> findByEntityType(String entityType);

    @Query("{ 'performedBy.id': ?0 }")
    List<AuditLog> findByPerformedById(String userId);

    @Query("{ 'entityType': ?0, 'entityId': ?1 }")
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);

    @Query("{ 'performedAt': { $gte: ?0, $lte: ?1 } }")
    List<AuditLog> findByPerformedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("{ 'action': ?0, 'performedAt': { $gte: ?1, $lte: ?2 } }")
    List<AuditLog> findByActionAndPerformedAtBetween(String action, LocalDateTime start, LocalDateTime end);
}
