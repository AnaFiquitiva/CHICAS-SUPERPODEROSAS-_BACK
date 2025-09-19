package eci.edu.dosw.proyecto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import eci.edu.dosw.proyecto.model.SolicitudCambio;

public interface SolicitudCambioRepository extends MongoRepository<SolicitudCambio, String> {
}