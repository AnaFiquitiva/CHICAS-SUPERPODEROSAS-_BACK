package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    List<Group> findBySubjectId(String subjectId);

    Optional<Group> findByGroupCode(String groupCode);

    List<Group> findByIdIn(List<String> groupIds);

    List<Group> findBySubjectIdAndActive(String subjectId, Boolean active);
    List<Group> findByFaculty(Faculty faculty);
    List<Group> findByActiveTrue();
    List<Group> findBySubjectIdAndActiveTrue(String subjectId);
}