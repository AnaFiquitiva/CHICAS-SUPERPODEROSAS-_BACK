package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Administrator;
import eci.edu.dosw.proyecto.model.AdministratorType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministratorRepository extends MongoRepository<Administrator, String> {
    Optional<Administrator> findByEmployeeCode(String employeeCode);
    Optional<Administrator> findByEmail(String email);
    List<Administrator> findByDepartment(String department);
    List<Administrator> findByActive(Boolean active);
    List<Administrator> findByType(AdministratorType type);
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByEmail(String email);
}