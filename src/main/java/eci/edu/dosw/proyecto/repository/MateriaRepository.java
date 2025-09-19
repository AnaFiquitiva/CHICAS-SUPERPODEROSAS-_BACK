package eci.edu.dosw.proyecto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import eci.edu.dosw.proyecto.model.Materia;

public interface MateriaRepository extends MongoRepository<Materia, String> {
}
