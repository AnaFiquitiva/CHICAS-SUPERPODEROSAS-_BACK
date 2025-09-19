package eci.edu.dosw.proyecto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import eci.edu.dosw.proyecto.model.Decanatura;

public interface DecanaturaRepository extends MongoRepository<Decanatura, String> {
}
