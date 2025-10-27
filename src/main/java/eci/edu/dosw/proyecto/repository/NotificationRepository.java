package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Notification;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientAndReadFalse(User recipient);

    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    @Query("{ 'recipient.id': ?0, 'read': false }")
    List<Notification> findByRecipientIdAndReadFalse(String recipientId);

    @Query("{ 'recipient.id': ?0 }")
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);

    @Query("{ 'relatedRequest.id': ?0 }")
    List<Notification> findByRelatedRequestId(String requestId);

    @Query("{ 'type': ?0, 'createdAt': { $gte: ?1 } }")
    List<Notification> findByTypeAndCreatedAtAfter(String type, LocalDateTime date);

    long countByRecipientAndReadFalse(User recipient);
}
