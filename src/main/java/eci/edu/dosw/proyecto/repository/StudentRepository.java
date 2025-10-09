package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    Optional<Student> findByStudentCode(String studentCode);

    Optional<Student> findByEmail(String email);

    List<Student> findByNameContainingIgnoreCase(String name);

    List<Student> findByProgramContainingIgnoreCase(String program);

    @Query("{ '$or': [ { 'name': { $regex: ?0, $options: 'i' } }, { 'studentCode': { $regex: ?0, $options: 'i' } }, { 'program': { $regex: ?0, $options: 'i' } } ] }")
    List<Student> searchByKeyword(String keyword);

}