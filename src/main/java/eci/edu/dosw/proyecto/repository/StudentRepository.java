package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    Optional<Student> findByStudentCode(String studentCode);

    Optional<Student> findByEmail(String email);
}