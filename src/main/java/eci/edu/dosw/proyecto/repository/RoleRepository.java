package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);

    List<Role> findByNameIn(List<String> names);

    boolean existsByName(String name);

    @Query("{ 'name': { $in: ?0 }, 'active': true }")
    List<Role> findByNamesAndActive(List<String> names);
}
