package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ManualAssignmentRepository extends MongoRepository<ManualAssignment, String> {
    List<ManualAssignment> findByStudent(Student student);

    List<ManualAssignment> findByAssignedBy(User assignedBy);

    List<ManualAssignment> findByStatus(AssignmentStatus status);

    @Query("{ 'student.id': ?0 }")
    List<ManualAssignment> findByStudentId(String studentId);

    @Query("{ 'assignedBy.id': ?0 }")
    List<ManualAssignment> findByAssignedById(String assignedById);

    @Query("{ 'type': ?0, 'status': ?1 }")
    List<ManualAssignment> findByTypeAndStatus(AssignmentType type, AssignmentStatus status);

    @Query("{ 'student.program.faculty.id': ?0 }")
    List<ManualAssignment> findByFacultyId(String facultyId);

    @Query("{ 'subject.id': ?0 }")
    List<ManualAssignment> findBySubjectId(String subjectId);

    @Query("{ 'group.id': ?0 }")
    List<ManualAssignment> findByGroupId(String groupId);

    @Query("{ 'assignedAt': { $gte: ?0, $lte: ?1 } }")
    List<ManualAssignment> findByAssignedAtBetween(LocalDateTime start, LocalDateTime end);
}
