package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {
    Optional<Subject> findByCode(String code);

    List<Subject> findByFacultyAndActiveTrue(Faculty faculty);

    List<Subject> findByActiveTrue();

    @Query("{ 'faculty.id': ?0, 'active': true }")
    List<Subject> findByFacultyIdAndActiveTrue(String facultyId);

    @Query("{ '$or': [ { 'code': { '$regex': ?0, '$options': 'i' } }, { 'name': { '$regex': ?0, '$options': 'i' } } ], 'active': true }")
    List<Subject> searchSubjects(String searchTerm);

    @Query("{ 'prerequisites.id': ?0, 'active': true }")
    List<Subject> findSubjectsThatRequirePrerequisite(String subjectId);

    boolean existsByCode(String code);
}
