package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.StudentSchedule;
import eci.edu.dosw.proyecto.model.Subject;
import eci.edu.dosw.proyecto.model.SubjectProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectProgressRepository extends MongoRepository<SubjectProgress, String> {

    List<SubjectProgress> findBySubject(Subject subject);

    @Query("{ 'studentSchedule.id': ?0 }")
    List<SubjectProgress> findByStudentScheduleId(String studentScheduleId);

    @Query("{ 'subject.id': ?0, 'status': 'APPROVED' }")
    List<SubjectProgress> findApprovedBySubjectId(String subjectId);

    @Query("{ 'studentSchedule.student.id': ?0, 'subject.id': ?1 }")
    Optional<SubjectProgress> findByStudentIdAndSubjectId(String studentId, String subjectId);

    @Query("{ 'studentSchedule.student.id': ?0, 'status': ?1 }")
    List<SubjectProgress> findByStudentIdAndStatus(String studentId, String status);
}
