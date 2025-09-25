package eci.edu.dosw.proyecto.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import eci.edu.dosw.proyecto.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByCodigo(String codigo);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(RolUsuario rol);
    List<Usuario> findByActivoTrue();

    @Query("{ 'rol': ?0, 'activo': true }")
    List<Usuario> findActivosByRol(RolUsuario rol);

    boolean existsByCodigo(String codigo);
    boolean existsByEmail(String email);

    @Query("{ 'fechaUltimoAcceso': { $lt: ?0 } }")
    List<Usuario> findUsuariosInactivos(java.time.LocalDateTime fechaLimite);
}
