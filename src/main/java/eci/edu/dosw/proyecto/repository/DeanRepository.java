package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeanRepository extends MongoRepository<Dean, String> {
    Optional<Dean> findByInstitutionalEmail(String institutionalEmail);

    Optional<Dean> findByUser(User user);

    List<Dean> findByFacultyAndActiveTrue(Faculty faculty);

    List<Dean> findByActiveTrue();

    @Query("{ 'faculty.id': ?0, 'active': true }")
    List<Dean> findByFacultyIdAndActiveTrue(String facultyId);

    boolean existsByInstitutionalEmail(String institutionalEmail);
}
