package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.StudyPlanProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanProgressRepository extends MongoRepository<StudyPlanProgress, String> {
    Optional<StudyPlanProgress> findByStudent(Student student);

    @Query("{ 'student.id': ?0 }")
    Optional<StudyPlanProgress> findByStudentId(String studentId);

    @Query("{ 'student.program.id': ?0 }")
    List<StudyPlanProgress> findByProgramId(String programId);

    @Query("{ 'student.program.faculty.id': ?0 }")
    List<StudyPlanProgress> findByFacultyId(String facultyId);

    @Query("{ 'progressPercentage': { $gte: ?0 } }")
    List<StudyPlanProgress> findByProgressPercentageGreaterThanEqual(Double percentage);
}
