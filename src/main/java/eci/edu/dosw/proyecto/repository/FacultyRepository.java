package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Faculty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends MongoRepository<Faculty, String> {
    Optional<Faculty> findByCode(String code);

    List<Faculty> findByActiveTrue();

    @Query("{ 'code': { '$regex': ?0, '$options': 'i' }, 'active': true }")
    List<Faculty> findByCodeContainingIgnoreCase(String code);

    boolean existsByCode(String code);
}
