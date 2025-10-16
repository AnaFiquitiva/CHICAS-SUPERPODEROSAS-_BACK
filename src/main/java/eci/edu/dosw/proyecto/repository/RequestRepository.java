package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Request;
import eci.edu.dosw.proyecto.model.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RequestRepository extends MongoRepository<Request, String> {

    // Buscar todas las solicitudes de una facultad
    List<Request> findByFaculty(String faculty);

    // Buscar todas las solicitudes de un estudiante específico
    List<Request> findByStudent_Id(String studentId);

    // Buscar por estado dentro de una facultad (útil para filtros en el panel de decanatura)
    List<Request> findByFacultyAndStatus(String faculty, RequestStatus status);
}
