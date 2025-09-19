package eci.edu.dosw.proyecto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import eci.edu.dosw.proyecto.model.Grupo;

public interface GrupoRepository extends MongoRepository<Grupo, String> {
}