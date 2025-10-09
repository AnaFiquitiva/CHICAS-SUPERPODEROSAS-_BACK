package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.ExceptionalCase;
import eci.edu.dosw.proyecto.model.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExceptionalCaseRepository extends MongoRepository<ExceptionalCase, String> {

    // Métodos similares a ChangeRequestRepository pero para casos excepcionales
    List<ExceptionalCase> findByStudentId(String studentId);
    List<ExceptionalCase> findByStatus(RequestStatus status);
    List<ExceptionalCase> findByAssignedTo(String assignedTo);
    List<ExceptionalCase> findByCaseType(String caseType);
    List<ExceptionalCase> findByPriority(Integer priority);

    @Query("{ 'studentId': ?0, 'status': { $in: ['PENDING', 'UNDER_REVIEW'] } }")
    List<ExceptionalCase> findActiveCasesByStudent(String studentId);

    @Query("{ 'creationDate': { $gte: ?0 } }")
    List<ExceptionalCase> findRecentCases(LocalDateTime date);

    @Query("{ 'status': { $in: ['PENDING', 'UNDER_REVIEW'] } }")
    List<ExceptionalCase> findPendingCases();

    Optional<ExceptionalCase> findByCaseNumber(String caseNumber);

    @Query(value = "{ 'studentId': ?0 }", count = true)
    Long countByStudentId(String studentId);

    @Query(value = "{ 'status': ?0 }", count = true)
    Long countByStatus(RequestStatus status);

    @Query("{ 'responseDeadline': { $lt: ?0 }, 'status': { $in: ['PENDING', 'UNDER_REVIEW', 'NEEDS_INFO'] } }")
    List<ExceptionalCase> findOverdueCases(LocalDateTime currentDate);

    // Método para contar casos activos por estudiante
    @Query(value = "{ 'studentId': ?0, 'status': { $in: ['PENDING', 'UNDER_REVIEW', 'NEEDS_INFO'] } }", count = true)
    Long countActiveCasesByStudent(String studentId);
}