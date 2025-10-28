package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Request;
import eci.edu.dosw.proyecto.model.RequestHistory;
import eci.edu.dosw.proyecto.model.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestHistoryRepository extends MongoRepository<RequestHistory, String> {
    List<RequestHistory> findByRequestOrderByChangedAtDesc(Request request);

    @Query("{ 'request.id': ?0 }")
    List<RequestHistory> findByRequestIdOrderByChangedAtDesc(String requestId);

    @Query("{ 'changedBy.id': ?0 }")
    List<RequestHistory> findByChangedById(String userId);

    @Query("{ 'request.student.program.faculty.id': ?0 }")
    List<RequestHistory> findByFacultyId(String facultyId);

    @Query("{ 'newStatus': ?0, 'changedAt': { $gte: ?1, $lte: ?2 } }")
    List<RequestHistory> findByNewStatusAndChangedAtBetween(RequestStatus status, LocalDateTime start, LocalDateTime end);
}
