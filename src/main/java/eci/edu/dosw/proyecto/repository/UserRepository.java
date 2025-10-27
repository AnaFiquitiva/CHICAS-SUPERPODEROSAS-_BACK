package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Role;
import eci.edu.dosw.proyecto.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRoleAndActiveTrue(Role role);

    List<User> findByActiveTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("{ 'role.name': ?0, 'active': true }")
    List<User> findByRoleName(String roleName);

    long countByRoleAndActiveTrue(Role role);
}
