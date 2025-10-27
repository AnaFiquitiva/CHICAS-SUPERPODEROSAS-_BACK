package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.Faculty;
import eci.edu.dosw.proyecto.model.RealTimeStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RealTimeStatsRepository extends MongoRepository<RealTimeStats, String> {
    Optional<RealTimeStats> findByStatTypeAndFaculty(String statType, Faculty faculty);

    @Query("{ 'statType': ?0, 'faculty.id': ?1 }")
    Optional<RealTimeStats> findByStatTypeAndFacultyId(String statType, String facultyId);

    @Query("{ 'statType': ?0, 'faculty': null }")
    Optional<RealTimeStats> findGlobalByStatType(String statType);

    @Query("{ 'calculatedAt': { $gte: ?0 } }")
    List<RealTimeStats> findByCalculatedAtAfter(LocalDateTime since);

    @Query("{ 'statType': { $in: ?0 } }")
    List<RealTimeStats> findByStatTypeIn(List<String> statTypes);
}
