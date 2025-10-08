package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {

    Optional<Subject> findByCode(String code);

    List<Subject> findByAcademicPlanIdsContaining(String academicPlanId);

    List<Subject> findByIdIn(List<String> subjectIds);
}