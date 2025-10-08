package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.ChangeRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChangeRequestRepository extends MongoRepository<ChangeRequest, String> {

    List<ChangeRequest> findByStudentId(String studentId);

    Optional<ChangeRequest> findByRequestNumber(String requestNumber);

    @Query("{ 'studentId': ?0, 'status': { $in: ['PENDING', 'UNDER_REVIEW'] } }")
    List<ChangeRequest> findActiveRequestsByStudent(String studentId);

    @Query(value = "{ 'studentId': ?0, 'status': { $in: ?1 } }", count = true)
    Long countByStudentIdAndStatusIn(String studentId, List<String> statuses);
}