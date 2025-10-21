package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByNameContainingIgnoreCase(String name);
    List<Course> findByCodeContainingIgnoreCase(String code);
    List<Course> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}
