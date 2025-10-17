/**
 * Repository for academic progress data access
 * Extends MongoDB repository for Student entity with custom queries
 */
package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface AcademicProgressRepository extends MongoRepository<Student, String> {

    /**
     * Finds student by student code
     * @param studentCode Unique student identifier code
     * @return Student entity if found
     */
    @Query("{ 'studentCode': ?0 }")
    Optional<Student> findByStudentCode(String studentCode);

    /**
     * Finds student by ID with enrollment information
     * @param studentId Student's unique identifier
     * @return Student entity with enrollments
     */
    @Query("{ 'id': ?0 }")
    Optional<Student> findByIdWithEnrollments(String studentId);
}