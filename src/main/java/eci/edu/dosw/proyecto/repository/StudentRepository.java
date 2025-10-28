package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Program;
import eci.edu.dosw.proyecto.model.Student;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByCode(String code);

    Optional<Student> findByInstitutionalEmail(String institutionalEmail);

    Optional<Student> findByUser(User user);

    List<Student> findByProgramAndActiveTrue(Program program);

    List<Student> findByProgramFacultyAndActiveTrue(String faculty);

    List<Student> findByCurrentSemesterAndActiveTrue(Integer semester);

    List<Student> findByProgramAndCurrentSemesterAndActiveTrue(Program program, Integer semester);

    @Query("{ 'active': true, '$or': [ { 'code': { '$regex': ?0, '$options': 'i' } }, { 'firstName': { '$regex': ?0, '$options': 'i' } }, { 'lastName': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Student> searchActiveStudents(String searchTerm);

    boolean existsByCode(String code);

    boolean existsByInstitutionalEmail(String institutionalEmail);

    long countByProgramAndActiveTrue(Program program);

    long countByProgramFacultyAndActiveTrue(String faculty);

    List<Student> findByActiveTrue();
}
