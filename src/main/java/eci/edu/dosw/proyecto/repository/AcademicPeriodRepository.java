package eci.edu.dosw.proyecto.repository;

import eci.edu.dosw.proyecto.model.AcademicPeriod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicPeriodRepository extends MongoRepository<AcademicPeriod, String> {

    @Query("{ 'isActive': true, 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 } }")
    List<AcademicPeriod> findActivePeriods(LocalDateTime date);

    Optional<AcademicPeriod> findByPeriodName(String periodName);
    Optional<AcademicPeriod> findByIsActiveTrue();
    List<AcademicPeriod> findByAllowGroupChangesTrue();
}