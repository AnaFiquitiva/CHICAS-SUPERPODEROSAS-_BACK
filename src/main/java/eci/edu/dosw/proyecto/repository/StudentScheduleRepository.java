package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.StudentSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentScheduleRepository extends MongoRepository<StudentSchedule, String> {
    Optional<StudentSchedule> findByStudentAndAcademicPeriod(Student student, String academicPeriod);

    List<StudentSchedule> findByStudent(Student student);

    List<StudentSchedule> findByAcademicPeriod(String academicPeriod);

    @Query("{ 'student.id': ?0, 'academicPeriod': ?1 }")
    Optional<StudentSchedule> findByStudentIdAndAcademicPeriod(String studentId, String academicPeriod);

    @Query("{ 'enrolledGroups.id': ?0 }")
    List<StudentSchedule> findByEnrolledGroupId(String groupId);

    @Query("{ 'student.program.faculty.id': ?0, 'academicPeriod': ?1 }")
    List<StudentSchedule> findByFacultyIdAndAcademicPeriod(String facultyId, String academicPeriod);

    @Query("{ 'student.id': ?0 }")
    List<StudentSchedule> findByStudentId(String studentId);


}
