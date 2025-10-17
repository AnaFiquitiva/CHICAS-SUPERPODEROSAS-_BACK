package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.ChangeRequest;
import eci.edu.dosw.proyecto.model.RequestStatus;
import eci.edu.dosw.proyecto.model.RequestType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    // Consultas por estado
    List<ChangeRequest> findByStatus(RequestStatus status);

    @Query(value = "{ 'status': ?0 }", count = true)
    Long countByStatus(RequestStatus status);

    // Consultas por tipo
    List<ChangeRequest> findByType(RequestType type);

    @Query(value = "{ 'type': ?0 }", count = true)
    Long countByType(RequestType type);

    // Consultas por fecha
    @Query("{ 'creationDate': { $gte: ?0 } }")
    List<ChangeRequest> findByCreationDateAfter(LocalDateTime date);

    @Query(value = "{ 'creationDate': { $gte: ?0 } }", count = true)
    Long countByCreationDateAfter(LocalDateTime date);

    @Query("{ 'creationDate': { $gte: ?0, $lte: ?1 } }")
    List<ChangeRequest> findByCreationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Consultas combinadas estudiante + estado
    @Query("{ 'studentId': ?0, 'status': ?1 }")
    List<ChangeRequest> findByStudentIdAndStatus(String studentId, RequestStatus status);

    // Consultas combinadas estudiante + tipo
    @Query("{ 'studentId': ?0, 'type': ?1 }")
    List<ChangeRequest> findByStudentIdAndType(String studentId, RequestType type);

    // Consultas por estudiante + ID específico
    @Query("{ 'studentId': ?0, 'id': ?1 }")
    Optional<ChangeRequest> findByStudentIdAndId(String studentId, String requestId);

    // Consultas por estudiante + número de solicitud
    @Query("{ 'studentId': ?0, 'requestNumber': ?1 }")
    Optional<ChangeRequest> findByStudentIdAndRequestNumber(String studentId, String requestNumber);

    // Contadores para estadísticas
    @Query(value = "{}", count = true)
    Long countTotalRequests();

    @Query(value = "{ 'studentId': ?0 }", count = true)
    Long countByStudentId(String studentId);

    @Query(value = "{ 'studentId': ?0, 'status': ?1 }", count = true)
    Long countByStudentIdAndStatus(String studentId, RequestStatus status);

    @Query(value = "{ 'studentId': ?0, 'type': ?1 }", count = true)
    Long countByStudentIdAndType(String studentId, RequestType type);

    // Consulta para solicitudes con prioridad
    @Query("{ 'priority': { $gt: 1 } }")
    List<ChangeRequest> findUrgentRequests();

    // Consulta para solicitudes por programa (necesita join con estudiantes)
    @Query(value = "{ 'studentId': { $in: ?0 } }")
    List<ChangeRequest> findByStudentIds(List<String> studentIds);

}