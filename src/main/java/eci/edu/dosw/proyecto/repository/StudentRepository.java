package eci.edu.dosw.proyecto.repository;
import eci.edu.dosw.proyecto.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
}