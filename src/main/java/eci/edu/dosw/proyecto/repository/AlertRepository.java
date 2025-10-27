package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Alert;
import eci.edu.dosw.proyecto.model.AlertType;
import eci.edu.dosw.proyecto.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert> findByResolvedFalse();

    List<Alert> findByGroupAndResolvedFalse(Group group);

    List<Alert> findByTypeAndResolvedFalse(AlertType type);

    @Query("{ 'group.id': ?0, 'resolved': false }")
    List<Alert> findByGroupIdAndResolvedFalse(String groupId);

    @Query("{ 'createdAt': { $gte: ?0 }, 'resolved': false }")
    List<Alert> findUnresolvedAlertsSince(LocalDateTime since);

    @Query("{ 'type': ?0, 'group.id': ?1, 'resolved': false }")
    Optional<Alert> findUnresolvedAlertByTypeAndGroup(AlertType type, String groupId);

    long countByResolvedFalse();

    @Query("{ 'group.faculty.$id': ?0, 'resolved': false }")
    List<Alert> findByFacultyIdAndResolvedFalse(String facultyId);


}
