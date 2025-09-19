package eci.edu.dosw.proyecto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import eci.edu.dosw.proyecto.model.Estudiante;

public interface EstudianteRepository extends MongoRepository<Estudiante, String> {

}
