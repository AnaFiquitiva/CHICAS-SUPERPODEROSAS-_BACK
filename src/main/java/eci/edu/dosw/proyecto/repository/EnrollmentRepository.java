package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Enrollment;
import eci.edu.dosw.proyecto.model.EnrollmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {

    List<Enrollment> findByStudentId(String studentId);

    List<Enrollment> findByStudentIdAndStatus(String studentId, EnrollmentStatus status);

    Optional<Enrollment> findByStudentIdAndGroupIdAndStatus(String studentId, String groupId, EnrollmentStatus status);

    List<Enrollment> findByGroupIdAndStatus(String groupId, EnrollmentStatus status);

    Long countByGroupIdAndStatus(String groupId, EnrollmentStatus status);

    List<Enrollment> findByGroupId(String groupId);
}