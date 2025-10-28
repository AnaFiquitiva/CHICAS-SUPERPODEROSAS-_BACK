package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.SystemConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigRepository extends MongoRepository<SystemConfig, String> {
    @Query("{ 'active': true }")
    Optional<SystemConfig> findActiveConfig();
}
