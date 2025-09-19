package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    // Métodos personalizados si es necesario
}
