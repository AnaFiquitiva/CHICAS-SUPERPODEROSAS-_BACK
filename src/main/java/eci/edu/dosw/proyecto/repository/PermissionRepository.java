package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends MongoRepository<Permission, String> {
    Optional<Permission> findByName(String name);

    List<Permission> findByNameIn(List<String> names);
}
