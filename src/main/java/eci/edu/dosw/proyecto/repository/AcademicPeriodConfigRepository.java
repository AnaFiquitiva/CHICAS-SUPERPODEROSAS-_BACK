package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.AcademicPeriodConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicPeriodConfigRepository extends MongoRepository<AcademicPeriodConfig, String> {
    List<AcademicPeriodConfig> findByActiveTrue();

    @Query("{ 'faculty.id': ?0, 'active': true }")
    List<AcademicPeriodConfig> findByFacultyIdAndActiveTrue(String facultyId);

    @Query("{ 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 }, 'active': true }")
    List<AcademicPeriodConfig> findActiveConfigsByDate(LocalDateTime date);

    @Query("{ 'faculty.id': ?0, 'startDate': { $lte: ?1 }, 'endDate': { $gte: ?1 }, 'active': true }")
    Optional<AcademicPeriodConfig> findActiveConfigByFacultyAndDate(String facultyId, LocalDateTime date);
}
