package eci.edu.dosw.proyecto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import eci.edu.dosw.proyecto.model.Administrador;

public interface AdministradorRepository extends MongoRepository<Administrador, String> {
}
