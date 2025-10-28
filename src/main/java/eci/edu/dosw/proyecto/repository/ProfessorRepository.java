package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Professor;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends MongoRepository<Professor, String> {
    Optional<Professor> findByCode(String code);

    Optional<Professor> findByInstitutionalEmail(String institutionalEmail);

    Optional<Professor> findByUser(User user);

    List<Professor> findByFacultyAndActiveTrue(Faculty faculty);

    List<Professor> findByActiveTrue();

    @Query("{ 'faculty.id': ?0, 'active': true }")
    List<Professor> findByFacultyIdAndActiveTrue(String facultyId);

    boolean existsByCode(String code);

    boolean existsByInstitutionalEmail(String institutionalEmail);
}
