package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.AcademicPlan;
import eci.edu.dosw.proyecto.model.StudentType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicPlanRepository extends MongoRepository<AcademicPlan, String> {

    List<AcademicPlan> findByStudentTypeAndProgramAndActive(StudentType studentType, String program, Boolean active);

    List<AcademicPlan> findByActive(Boolean active);
}