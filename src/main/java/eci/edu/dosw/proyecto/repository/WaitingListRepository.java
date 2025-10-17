package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.WaitingListEntry;
import eci.edu.dosw.proyecto.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio para gestionar entradas de listas de espera.
 * Permite consultar las entradas ordenadas por fecha de solicitud (de la más antigua a la más reciente).
 */
public interface WaitingListRepository extends MongoRepository<WaitingListEntry, String> {
    List<WaitingListEntry> findByGroupIdOrderByRequestDateAsc(String groupId);
    void deleteByStudentIdAndGroupId(String studentId, String groupId);
    List<WaitingListEntry> findByGroupId(String groupId);
    List<WaitingListEntry> findByStudentId(String studentId);
}
