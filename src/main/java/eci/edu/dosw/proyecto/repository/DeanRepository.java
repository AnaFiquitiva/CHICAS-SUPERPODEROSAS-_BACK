package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Dean;
import eci.edu.dosw.proyecto.model.DeanType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeanRepository extends MongoRepository<Dean, String> {
    Optional<Dean> findByEmployeeCode(String employeeCode);
    Optional<Dean> findByEmail(String email);
    List<Dean> findByFaculty(String faculty);
    List<Dean> findByType(DeanType type);
    List<Dean> findByActive(Boolean active);
    List<Dean> findByProgramsContaining(String program);
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByEmail(String email);
}