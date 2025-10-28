package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Group;
import eci.edu.dosw.proyecto.model.GroupCapacityHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupCapacityHistoryRepository extends MongoRepository<GroupCapacityHistory, String> {
    List<GroupCapacityHistory> findByGroupOrderByModifiedAtDesc(Group group);

    @Query("{ 'group.id': ?0 }")
    List<GroupCapacityHistory> findByGroupIdOrderByModifiedAtDesc(String groupId);

    @Query("{ 'modifiedBy.id': ?0 }")
    List<GroupCapacityHistory> findByModifiedById(String userId);

    @Query("{ 'group.subject.faculty.id': ?0 }")
    List<GroupCapacityHistory> findByFacultyId(String facultyId);

    @Query("{ 'modifiedAt': { $gte: ?0, $lte: ?1 } }")
    List<GroupCapacityHistory> findByModifiedAtBetween(LocalDateTime start, LocalDateTime end);
}
