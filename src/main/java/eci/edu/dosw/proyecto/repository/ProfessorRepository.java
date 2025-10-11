package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Professor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProfessorRepository extends MongoRepository<Professor, String> {
    List<Professor> findByActiveTrue();
    List<Professor> findByFacultyIdAndActiveTrue(String facultyId);
    List<Professor> findBySubjectIdsContainingAndActiveTrue(String subjectId);
}
