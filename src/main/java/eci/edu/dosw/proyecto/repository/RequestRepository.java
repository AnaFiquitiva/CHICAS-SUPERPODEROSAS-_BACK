package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends MongoRepository<Request, String> {
    Optional<Request> findByRequestNumber(String requestNumber);

    
    List<Request> findByStudent(Student student);

    List<Request> findByStatus(RequestStatus status);

    List<Request> findByType(RequestType type);

    List<Request> findByStudentAndStatus(Student student, RequestStatus status);

    @Query("{ 'student.id': ?0 }")
    List<Request> findByStudentId(String studentId);

    @Query("{ 'student.program.faculty.id': ?0 }")
    List<Request> findByFacultyId(String facultyId);

    @Query("{ 'requestedGroup.id': ?0 }")
    List<Request> findByRequestedGroupId(String groupId);

    @Query("{ 'currentGroup.id': ?0 }")
    List<Request> findByCurrentGroupId(String groupId);

    @Query("{ 'student.program.faculty.id': ?0, 'status': ?1 }")
    List<Request> findByFacultyIdAndStatus(String facultyId, RequestStatus status);

    @Query("{ 'student.program.faculty.id': ?0, 'status': { $in: ?1 } }")
    List<Request> findByFacultyIdAndStatusIn(String facultyId, List<RequestStatus> statuses);

    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Request> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("{ 'student.program.faculty.id': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Request> findByFacultyIdAndCreatedAtBetween(String facultyId, LocalDateTime start, LocalDateTime end);

    @Query(value = "{ 'student.program.faculty.id': ?0, 'status': ?1 }", sort = "{ 'createdAt': 1 }")
    List<Request> findByFacultyIdAndStatusOrderByCreatedAtAsc(String facultyId, RequestStatus status);

    @Query(value = "{ 'status': ?0 }", sort = "{ 'createdAt': 1 }")
    List<Request> findByStatusOrderByCreatedAtAsc(RequestStatus status);

    @Query("{ 'specialApproval': true }")
    List<Request> findSpecialApprovalCases();

    long countByStudentAndStatus(Student student, RequestStatus status);

    long countByStatus(RequestStatus status);

    long countByStudentProgramFacultyAndStatus(String faculty, RequestStatus status);

    long countByStudentProgramFaculty(String facultyId);

    long countByStudentProgramFacultyAndStatusAndCreatedAtBetween(Faculty student_program_faculty, RequestStatus status, LocalDateTime createdAt, LocalDateTime createdAt2);

    // Para contar solicitudes por facultad y período
    @Query("{ 'student.program.faculty.id': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    long countByStudentProgramFacultyAndCreatedAtBetween(String facultyId, LocalDateTime start, LocalDateTime end);

    @Query("{ 'student.program.faculty.id': ?0, 'status': ?1, 'createdAt': { $gte: ?2, $lte: ?3 } }")
    long countByStudentProgramFacultyAndStatusAndCreatedAtBetween(String facultyId, RequestStatus status, LocalDateTime start, LocalDateTime end);


    @Query(value = "{ 'student.program.faculty.id': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Request> findMostRequestedGroupsByFacultyAndPeriod(String facultyId, LocalDateTime startDate, LocalDateTime endDate);

    // En RequestRepository.java
    @Query("{ 'requestedGroup.id': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    long countByRequestedGroupAndCreatedAtBetween(String groupId, LocalDateTime start, LocalDateTime end);

    @Query("{ 'requestedGroup.id': ?0, 'status': ?1, 'createdAt': { $gte: ?2, $lte: ?3 } }")
    long countByRequestedGroupAndStatusAndCreatedAtBetween(String groupId, RequestStatus status, LocalDateTime start, LocalDateTime end);
}