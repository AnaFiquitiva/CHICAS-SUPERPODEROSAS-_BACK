package eci.edu.dosw.proyecto.repository;
import eci.edu.dosw.proyecto.model.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio para periodos académicos
 */
public interface AcademicPeriodRepository extends MongoRepository<AcademicPeriod, String> {

    @Query("{ 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 }, 'isActive': true }")
    Optional<AcademicPeriod> findActivePeriod(LocalDateTime date);

    Optional<AcademicPeriod> findByPeriodNameAndIsActiveTrue(String periodName);
}