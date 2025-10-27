package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Program;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends MongoRepository<Program, String> {
    Optional<Program> findByCode(String code);

    List<Program> findByFacultyAndActiveTrue(Faculty faculty);

    List<Program> findByActiveTrue();

    @Query("{ 'faculty.id': ?0, 'active': true }")
    List<Program> findByFacultyIdAndActiveTrue(String facultyId);

    @Query("{ '$or': [ { 'code': { '$regex': ?0, '$options': 'i' } }, { 'name': { '$regex': ?0, '$options': 'i' } } ], 'active': true }")
    List<Program> searchPrograms(String searchTerm);

    boolean existsByCode(String code);
}
